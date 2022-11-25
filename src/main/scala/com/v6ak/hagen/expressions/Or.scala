package com.v6ak.hagen.expressions

final class Or(a: Expr[Boolean], b: Expr[Boolean]) extends ConditionalBinOp("or")(a, b):
  override def asConditionalExpression: CondExpr[Boolean] = CondExpr(condition = a, ifTrue = Const(true), ifFalse = b)

  override def transform(transformer: Transformer): Expr[Boolean] = transformer.transformBottomUp(
    Or(
      transformer.transformTopDown(a).transform(transformer),
      transformer.transformTopDown(b).transform(transformer),
    )
  )

object Or:

  def of(seq: Seq[Expr[Boolean]]) = seq match
    case Seq() => Const(false)
    case Seq(first, others*) => others.foldLeft(first)(_ || _)
