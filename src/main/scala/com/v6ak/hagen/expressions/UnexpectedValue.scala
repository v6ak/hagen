package com.v6ak.hagen.expressions

final class UnexpectedValue[T] extends Expr[T] with NonTransformable[T]:
  override def asJinja(context: Context): String = "unexpected_value()"

  override def variables: Set[Entity[_]] = Set()
