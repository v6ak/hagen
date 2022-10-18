package com.v6ak.hagen.expressions

trait Transformer:
  // TODO: rethink
  def transformTopDown[T](expr: Expr[T]): Expr[T]

  def transformBottomUp[T](expr: Expr[T]): Expr[T]
