package com.v6ak.hagen.extensions.brightnessButtons

import com.v6ak.hagen.automation.Trigger
import com.v6ak.hagen.expressions.Expr

final case class BrighnessAutomationSource[C, B, T](
  triggerGen: C => Trigger[T],
  triggerToButton: Expr[T] => Expr[B],
  brightnessDownButton: Expr[B],
  brightnessUpButton: Expr[B],
  brightnessStopButtons: Seq[Expr[B]],
):
  if (brightnessStopButtons.isEmpty) {
    throw Exception("At least one stop button expected")
  }
