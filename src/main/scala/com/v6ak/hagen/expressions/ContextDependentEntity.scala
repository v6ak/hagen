package com.v6ak.hagen.expressions

/**
 * Context dependent entities use a name that might depend on Context. Most of the time, you probably want to use
 * Entity, which guarantees constant name regardless of the Context.
 */
abstract class ContextDependentEntity[T]()(implicit val jinjaType: Type[T]) extends DelegateExpr[T] with NonTransformable[T]:

  def getName(context: Context): String

  override protected def delegate(context: Context): Expr[T] = jinjaType.convert(
    unsafe.FuncCall("states", Const(getName(context)))
  )
