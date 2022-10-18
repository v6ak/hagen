package com.v6ak.hagen.expressions.unsafe

import com.v6ak.hagen.expressions.{Context, Entity, Expr, Transformer}

final class FilterExpr[I, O](a: Expr[I], filter: String) extends Expr[O]:
  override def asJinja(context: Context): String = s"${a.asContextSafeJinja(context)} | $filter"

  override def transform(transformer: Transformer): Expr[O] = transformer.transformBottomUp(
    FilterExpr(
      transformer.transformTopDown(a).transform(transformer),
      filter,
    )
  )

  override def variables: Set[Entity[_]] = a.variables
