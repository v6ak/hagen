package com.v6ak.hagen.extensions

import com.v6ak.hagen.SensorDef
import com.v6ak.hagen.automation.{Automation, Change}
import com.v6ak.hagen.conditions.TemplateCondition
import com.v6ak.hagen.expressions.*
import com.v6ak.hagen.expressions.BooleanOps.*
import com.v6ak.hagen.expressions.DoubleOps.*

package object hysteresis:
  def hysteresisSensor(
    name: String, source: Entity[Double], low: Expr[Double], high: Expr[Double], aboveHigh: Boolean
  ): SensorDef[HysteresisStatus] = {
    SensorDef(
      name,
      Assign(source)(value => {
        (value >= high).matches(
          ifTrue = Const(HysteresisStatus.from(aboveHigh)),
          ifFalse = (value < low).matches(
            ifTrue = Const(HysteresisStatus.from(!aboveHigh)),
            ifFalse = Const(HysteresisStatus.Keep)
          )
        )
      })
    )
  }

  def hysteresisAutomation(
    namePrefix: String,
    aliasPrefix: String,
    additionalTriggers: Seq[Change[Boolean]],
    additionalConditions: Seq[TemplateCondition],
    switchable: SwitchableEntity,
    hysteresisEntity: Entity[HysteresisStatus],
  ): Seq[Automation] = {
    Seq(true, false).map(state =>
      Automation(
        id = s"${namePrefix}_$state",
        alias = s"${aliasPrefix} ${HysteresisStatus.from(state)} (hagen)",
        conditions = Seq(TemplateCondition(
          hysteresisEntity === Const(HysteresisStatus.from(state)) && // when there is an action to do
            switchable === Const(!state) // don't reapply the same mode, it would just beep
        )) ++ additionalConditions,
        triggers = Seq(
          // when anything changes, reevaluate the conditions
          Change(hysteresisEntity).to(HysteresisStatus.from(state)),
          Change(switchable).to(!state),
        ) ++ additionalTriggers,
        actions = Seq(
          switchable.turn(state)
        )
      )
    )
  }
  
  def combineHysteresisActionsPreferOnUnlessUnknown(seq: Seq[Expr[HysteresisStatus]]): Expr[HysteresisStatus] = AssignSeq(seq) { seq =>
    def allAre(value: HysteresisStatus) = Or.of(seq.map(_ === Const(value)))
    allAre(HysteresisStatus.On).matches(
      ifTrue = Const(HysteresisStatus.On),
      ifFalse = allAre(HysteresisStatus.Keep).matches(
        ifTrue = Const(HysteresisStatus.Keep),
        ifFalse = Const(HysteresisStatus.Off)
      )
    )
  }
