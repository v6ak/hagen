package com.v6ak.hagen.expressions

final class CondExpr[O](condition: Expr[Boolean], ifTrue: Expr[O], ifFalse: Expr[O]) extends Expr[O]:
  override def asJinja(context: Context): String =
    s"${ifTrue.asContextSafeJinja(context)} " +
      s"if ${condition.asContextSafeJinja(context)} " +
      s"else ${ifFalse.asContextSafeJinja(context)}"

  override def transform(transformer: Transformer): Expr[O] = transformer.transformBottomUp(
    CondExpr(
      transformer.transformTopDown(condition).transform(transformer),
      transformer.transformTopDown(ifTrue).transform(transformer),
      transformer.transformTopDown(ifFalse).transform(transformer)
    )
  )

  override def variables: Set[Entity[_]] = condition.variables ++ ifTrue.variables ++ ifFalse.variables
