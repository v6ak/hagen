package com.v6ak.hagen.expressions

import com.v6ak.hagen.expressions

final case class StateAttr[T](entity: ContextDependentEntity[?], name: String) extends DelegateExpr[T] with NonTransformable[T]:
  override protected def delegate(context: Context): Expr[T] = expressions.unsafe.FuncCall[T](
    "state_attr", Const(entity.getName(context)), Const(name)
  )

  override def variables: Set[Entity[_]] = entity.variables
