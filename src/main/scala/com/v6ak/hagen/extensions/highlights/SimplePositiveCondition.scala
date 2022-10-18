package com.v6ak.hagen.extensions.highlights

import com.v6ak.hagen.Element
import com.v6ak.hagen.automation.{Change, Trigger}
import com.v6ak.hagen.expressions.{Const, Context, Entity, Type}

final case class SimplePositiveCondition[T](entity: Entity[T], value: T)(implicit jinjaType: Type[T]) extends SimpleCondition[T]:
  override def toStructure(context: Context) = Map(
    "entity" -> entity.name,
    "state" -> entity.jinjaType.serialize(value),
  )

  override def toImperfectTrigger: Trigger = Change(entity).to(value)

  override def toExpr = entity === Const(value)

  override def variables: Set[Entity[_]] = Set(entity)

  override def unary_! : SimpleCondition[T] = SimpleNegativeCondition(entity, value)

  override def defined: Set[Entity[_]] = Set()
