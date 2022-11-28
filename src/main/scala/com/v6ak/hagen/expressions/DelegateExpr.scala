package com.v6ak.hagen.expressions

trait DelegateExpr[T] extends Expr[T]:
  protected def delegate(context: Context): Expr[T]

  override def asJinja(context: Context): String = delegate(context).asJinja(context)

  override def asContextSafeJinja(context: Context): String = delegate(context).asContextSafeJinja(context)

