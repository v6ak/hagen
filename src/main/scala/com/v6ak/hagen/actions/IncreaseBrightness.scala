package com.v6ak.hagen.actions

import com.v6ak.hagen.expressions.{Context, Entity, Light}

final case class IncreaseBrightness(light: Light) extends Action:
  override def toStructure(context: Context): Any = Map(
    "device_id" -> "xxx",
    "domain" -> "light",
    "entity_id" -> light.name,
    "type" -> "brightness_increase",
  )

  override def variables: Set[Entity[_]] = Set(light)
