package com.v6ak.hagen

package object expressions {
  def max[T <: Int|Double](expressions: Expr[T]*): Expr[T] = unsafe.FuncCall("max", expressions: _*)
  def min[T <: Int|Double](expressions: Expr[T]*): Expr[T] = unsafe.FuncCall("min", expressions: _*)
  def abs[T <: Int|Double](expression: Expr[T]): Expr[T] = unsafe.FilterExpr(expression, "abs")
}
