package com.v6ak.hagen.expressions.unsafe

import com.v6ak.hagen.expressions.{Context, Entity, Expr, NonTransformable}

final class RawExpr[T](
  expr: String,
  val variables: Set[Entity[_]]
) extends Expr[T] with NonTransformable[T]:
  override def asJinja(context: Context): String = expr
