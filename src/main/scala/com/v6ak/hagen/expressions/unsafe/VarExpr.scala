package com.v6ak.hagen.expressions.unsafe

import com.v6ak.hagen.expressions.{Context, ContextSafeSyntax, Entity, Expr, NonTransformable}

final class VarExpr[T](
  expr: String,
  val variables: Set[Entity[_]]
) extends Expr[T] with NonTransformable[T] with ContextSafeSyntax:
  override def asJinja(context: Context): String = expr
