package com.v6ak.hagen.dashboards

import com.v6ak.hagen.expressions.{Context, Entity}
import com.v6ak.hagen.{Element, optionalMap}

case class StatsEntity[T](
  entity: Entity[T],
  name: Option[String] = None,
) extends Element:
  override def toStructure(context: Context): Any = Map(
    "entity" -> entity.name
  ) ++ optionalMap("name", name)

  override def variables: Set[Entity[_]] = entity.variables

  override def defined: Set[Entity[_]] = Set()
