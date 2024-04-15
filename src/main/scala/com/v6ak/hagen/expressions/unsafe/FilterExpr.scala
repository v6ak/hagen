package com.v6ak.hagen.expressions.unsafe

import com.v6ak.hagen.expressions.{Context, Entity, Expr, Transformer}



final class FilterExpr[I, O](a: Expr[I], filter: FuncCall[O]|VarExpr[O]) extends Expr[O]:
  override def asJinja(context: Context): String = s"${a.asContextSafeJinja(context)} | ${filter.asContextSafeJinja(context)}"

  override def transform(transformer: Transformer): Expr[O] = transformer.transformBottomUp(
    new FilterExpr(
      transformer.transformTopDown(a).transform(transformer),
      // TODO: consider some more specific transformation that don't require the type case
      transformer.transformTopDown(filter).transform(transformer).asInstanceOf[FuncCall[O]|VarExpr[O]],
    )
  )

  override def variables: Set[Entity[_]] = a.variables ++ filter.variables

object FilterExpr:
  def apply[I, O](a: Expr[I], filter: String): FilterExpr[I, O] = new FilterExpr(a, VarExpr(filter, Set()))
  def apply[I, O](a: Expr[I], filterCall: FuncCall[O]): FilterExpr[I, O] = new FilterExpr(a, filterCall)