package com.v6ak.hagen.expressions

trait ContextSafeSyntax extends Expr[_]:
  override def asContextSafeJinja(context: Context): String = asJinja(context)
