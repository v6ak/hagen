package com.v6ak.hagen.expressions

import com.v6ak.hagen.expressions

final case class StateAttr[T](entity: Entity[?], name: String) extends DelegateExpr[T]:
  override protected def delegate: Expr[T] = expressions.unsafe.FuncCall[T](
    "state_attr", Const(entity.name), Const(name)
  )

  override def variables: Set[Entity[_]] = Set(entity)

  override def transform(transformer: Transformer): Expr[T] = delegate.transform(transformer)
