package com.v6ak.hagen.extensions

import com.v6ak.hagen.SensorDef
import com.v6ak.hagen.automation.{Automation, Change}
import com.v6ak.hagen.conditions.TemplateCondition
import com.v6ak.hagen.expressions.*

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
    def someIs(value: HysteresisStatus) = Or.of(seq.map(_ === Const(value)))
    someIs(HysteresisStatus.On).matches(
      ifTrue = Const(HysteresisStatus.On),
      ifFalse = someIs(HysteresisStatus.Keep).matches(
        ifTrue = Const(HysteresisStatus.Keep),
        ifFalse = Const(HysteresisStatus.Off)
      )
    )
  }

  def combineHysteresisActionsPreferExtremesAndOn(seq: Seq[Expr[HysteresisStatus]]): Expr[HysteresisStatus] = AssignSeq(seq) { seq =>
    combineHysteresisActions(seq, HysteresisStatus.On, HysteresisStatus.Off)
  }

  def combineHysteresisActions(seq: Seq[Expr[HysteresisStatus]], first: HysteresisStatus, second: HysteresisStatus): Expr[HysteresisStatus] = AssignSeq(seq) { seq =>
    val missingValues: Set[HysteresisStatus] = HysteresisStatus.values.toSet -- Seq(first, second)
    val third = missingValues.toSeq match
      case Seq(single) => single
      case other => throw IllegalArgumentException(s"First and second seem to be the same: $first, $second")
    def someIs(value: HysteresisStatus) = Or.of(seq.map(_ === Const(value)))
    someIs(first).matches(
      ifTrue = Const(first),
      ifFalse = someIs(second).matches(
        ifTrue = Const(second),
        ifFalse = Const(third)
      )
    )
  }
