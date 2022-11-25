package com.v6ak.hagen.expressions.unsafe

import com.v6ak.hagen.expressions.{Context, ContextSafeSyntax, Entity, Expr, Transformer}

class FieldExpr[I, O](a: Expr[I], field: String) extends Expr[O] with ContextSafeSyntax:
  override def asJinja(context: Context): String = s"${a.asContextSafeJinja(context)}.$field"

  override def transform(transformer: Transformer): Expr[O] = transformer.transformBottomUp(
    FieldExpr(
      transformer.transformTopDown(a).transform(transformer),
      field,
    )
  )

  override def variables: Set[Entity[_]] = a.variables

