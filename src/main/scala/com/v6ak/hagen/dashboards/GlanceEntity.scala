package com.v6ak.hagen.dashboards

import com.v6ak.hagen.expressions.{Context, Entity}
import com.v6ak.hagen.{Element, optionalMap}

case class GlanceEntity[T](
  entity: Entity[T],
  name: Option[String] = None,
  icon: Option[Icon] = None,
) extends Element:
  override def toStructure(context: Context): Any = Map(
    "entity" -> entity.name
  ) ++
    optionalMap("name", name) ++
    optionalMap("icon", icon.map(_.toStructure(context)))

  override def variables: Set[Entity[_]] = entity.variables

  override def defined: Set[Entity[_]] = Set()

