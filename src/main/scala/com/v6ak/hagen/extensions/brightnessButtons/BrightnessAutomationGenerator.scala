package com.v6ak.hagen.extensions.brightnessButtons

import com.v6ak.hagen.actions.{Action, ConditionalAction, While}
import com.v6ak.hagen.automation.{AutomationBase, ScriptMode, Trigger}
import com.v6ak.hagen.{actions, expressions}
import com.v6ak.hagen.expressions.*


final case class BrightnessAutomationGenerator[C, B, T](
  source: BrighnessAutomationSource[C, B, T],
  stepDuration: Expr[Double],
  brightnessChange: Expr[Double],
  boostExpr: Option[Expr[Double]] = None,
):

  def withTimeBasedBoost(f: Expr[Int] => Expr[Double]): BrightnessAutomationGenerator[C, B, T] = {
    val durationExpr = now() - CurrentAutomationEntity.LastTriggered
    val durationMicrosExpr: Expr[Int] = durationExpr.microseconds
    copy(boostExpr = Some(f(durationMicrosExpr)))
  }

  def withTimeLinearBoost(factor: Expr[Double]): BrightnessAutomationGenerator[C, B, T] =
    withTimeBasedBoost(durationMicrosExpr =>
      Const(1) + durationMicrosExpr * factor
    )

  def brightnessAutomation(control: C, light: Light): AutomationBase = {
    val allButtons = Seq(source.brightnessDownButton, source.brightnessUpButton) ++ source.brightnessStopButtons
    val brightnessChangeExpr = boostExpr match
      case Some(expr) => brightnessChange * expr
      case None => brightnessChange

    def branch(runCondition: Expr[Boolean], brightnessSign: Expr[Double]): Seq[Action] = Seq(
      // actions for each branch (increasing or decreasing brightness)
      While(
        condition = runCondition,
        sequence = Seq(
          light.changeBrightness(
            brightnessChangeExpr * brightnessSign,
            transition = Some(stepDuration),
          ),
          actions.unsafe.RawAction(Map("delay" -> stepDuration), Set()),
        )
      )
    )

    AutomationBase.useMode(ScriptMode.Restart)  // When any of the relevant event is triggered, it stops the execution
      .on(source.triggerGen(control))
      .providedThat(trigger => source.triggerToButton(trigger).in(allButtons*))
      .doAction { trigger =>
        val button = source.triggerToButton(trigger)
        ConditionalAction
          .when(button === source.brightnessDownButton)
          .doActions(
            branch(runCondition = light.brightnessRaw > Const(0), brightnessSign = Const(-1.0))
          )
          .when(button === source.brightnessUpButton)
          .doActions(branch(runCondition = light.brightnessRaw < Const(255), brightnessSign = Const(+1.0)))
        // If it is any of the stop buttons, just stop.
      }
  }
