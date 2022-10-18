package com.v6ak.hagen.expressions

trait NonTransformable[T] extends Expr[T]:
  override def transform(transformer: Transformer): Expr[T] = this
