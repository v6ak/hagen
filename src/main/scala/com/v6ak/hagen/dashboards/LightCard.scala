package com.v6ak.hagen.dashboards

import com.v6ak.hagen.optionalMap
import com.v6ak.hagen.expressions.{Context, Entity, Light}

case class LightCard(entity: Light, icon: Option[Icon]) extends Card:
  override def toStructure(context: Context): Any = Map(
    "type" -> "light",
    "entity" -> entity.name,
  ) ++
    optionalMap("icon", icon.map(_.toStructure))

  override def variables: Set[Entity[_]] = Set(entity)
