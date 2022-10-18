package com.v6ak.hagen.expressions

final class And(a: Expr[Boolean], b: Expr[Boolean]) extends ConditionalBinOp("and")(a, b):
  override def asConditionalExpression: CondExpr[Boolean] = CondExpr(condition = a, ifTrue = b, ifFalse = Const(false))

  override def transform(transformer: Transformer): Expr[Boolean] = transformer.transformBottomUp(
    And(
      transformer.transformTopDown(a).transform(transformer),
      transformer.transformTopDown(b).transform(transformer),
    )
  )

object And:

  import BooleanOps.&&
  def of(seq: Seq[Expr[Boolean]]) = seq match
    case Seq() => Const(true)
    case Seq(first, others*) => others.foldLeft(first)(_ && _)
