package com.v6ak.hagen.automation

import com.v6ak.hagen
import com.v6ak.hagen.expressions.{Context, Entity, StateAttr, Type}
import com.v6ak.hagen.optionalMap

import scala.concurrent.duration.Duration

final case class Change[T] private(
  entity: Entity[_],
  to: Option[T] = None,
  from: Option[T] = None,
  duration: Option[Duration] = None,
  attribute: Option[String] = None
)(implicit serializer: Type[T]) extends Trigger:
  def toStructure(context: Context) = Map(
    "platform" -> "state",
    "entity_id" -> Seq(entity.name),
  ) ++
    optionalMap("to", to.map(serializer.serialize)) ++
    optionalMap("from", from.map(serializer.serialize)) ++
    optionalMap("for", duration.map(duration => Map("seconds" -> duration.toSeconds))) ++
    optionalMap("attribute", attribute)


  def from(from: T): Change[T] = copy(from = Some(from))
  def to(to: T): Change[T] = copy(to = Some(to))

  def duration(duration: Duration): Change[T] = copy(duration = Some(duration))

  override def variables: Set[Entity[_]] = entity.variables

object Change {
  def apply[T](entity: Entity[T])(implicit serializer: Type[T]): Change[T] = new Change(entity)
  def apply[T](attr: StateAttr[T])(implicit serializer: Type[T]): Change[T] = new Change(attr.entity, attribute = Some(attr.name))
}