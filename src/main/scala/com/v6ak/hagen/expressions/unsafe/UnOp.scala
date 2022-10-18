package com.v6ak.hagen.expressions.unsafe

import com.v6ak.hagen.expressions.{Context, Entity, Expr, Transformer}

final class UnOp[O, A](operator: String)(a: Expr[A]) extends Expr[O]:
  override def asJinja(context: Context): String = s"$operator ${a.asContextSafeJinja(context)}"

  override def transform(transformer: Transformer): Expr[O] = transformer.transformBottomUp(
    UnOp(operator)(
      transformer.transformTopDown(a).transform(transformer)
    )
  )

  override def variables: Set[Entity[_]] = a.variables
