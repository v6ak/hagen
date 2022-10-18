package com.v6ak.hagen.expressions

final case class Const[T](value: T)(implicit jinjaType: Type[T])
  extends Expr[T] with NonTransformable[T] with ContextSafeSyntax:

  override def asJinja(context: Context): String = jinjaType.const(value)

  /*
  TODO: add tests and finish the implementation
  override def asCompleteJinja(context: Context): String = {
    if !asJinja(context).contains('{') then
      …
    else
      super.asCompleteJinja(context)
  }
  */

  override def variables: Set[Entity[_]] = Set()
