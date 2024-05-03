package com.v6ak.hagen.expressions

import com.v6ak.hagen.actions.ServiceCall

final case class InputNumber[T <: Int | Double](name: String)(implicit jinjaType: Type[T]) extends Entity[T]:
  def setValue(value: Expr[T]) = ServiceCall("input_number.set_value", this, Map("value" -> value))
