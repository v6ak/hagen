package com.v6ak.hagen.expressions

abstract class ConditionalBinOp(operator: String)(a: Expr[Boolean], b: Expr[Boolean]) extends Expr[Boolean]:
  // When implementing single-evaluation for Assign, we can easily convert all ConditionalBinOp to CondExpr,
  // and still keep the laziness of evaluation. We'll need just to care about CondExpr.
  override def asJinja(context: Context): String =
    s"${a.asContextSafeJinja(context)} $operator ${b.asContextSafeJinja(context)}"

  def asConditionalExpression: CondExpr[Boolean]

  override def variables: Set[Entity[_]] = a.variables ++ b.variables
