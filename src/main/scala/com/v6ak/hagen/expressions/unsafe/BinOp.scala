package com.v6ak.hagen.expressions.unsafe

import com.v6ak.hagen.expressions.{Context, Entity, Expr, Transformer}

final class BinOp[O, A, B](operator: String)(a: Expr[A], b: Expr[B]) extends Expr[O]:
  assert(operator != null)
  assert(a != null)
  assert(b != null)
  override def asJinja(context: Context): String =
    s"${a.asContextSafeJinja(context)} $operator ${b.asContextSafeJinja(context)}"

  override def transform(transformer: Transformer): Expr[O] = transformer.transformBottomUp(
    BinOp(operator)(
      transformer.transformTopDown(a).transform(transformer),
      transformer.transformTopDown(b).transform(transformer),
    )
  )

  override def variables: Set[Entity[_]] = a.variables ++ b.variables
