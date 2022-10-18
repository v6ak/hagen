package com.v6ak.hagen.expressions

trait DelegateExpr[T] extends Expr[T]:
  protected def delegate: Expr[T]

  override def asJinja(context: Context): String = delegate.asJinja(context)

  override def asContextSafeJinja(context: Context): String = delegate.asContextSafeJinja(context)
