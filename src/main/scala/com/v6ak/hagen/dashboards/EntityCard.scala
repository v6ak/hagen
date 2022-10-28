package com.v6ak.hagen.dashboards

import com.v6ak.hagen.optionalMap
import com.v6ak.hagen.expressions.{Context, Entity}

case class EntityCard(
  entity: Entity[_],
  icon: Option[Icon],
  attribute: Option[String],
  name: Option[String],
) extends Card:
  override def toStructure(context: Context): Any = (
    Map(
      "type" -> "entity",
      "entity" -> entity.name,
    ) ++
      optionalMap("icon", icon.map(_.toStructure)) ++
      optionalMap("attribute", attribute) ++
      optionalMap("name", name)
  )

  override def variables: Set[Entity[_]] = Set(entity)
