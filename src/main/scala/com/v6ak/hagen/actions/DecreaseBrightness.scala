package com.v6ak.hagen.actions

import com.v6ak.hagen.expressions.{Context, Entity, Light}

final case class DecreaseBrightness(light: Light) extends Action:
  override def toStructure(context: Context): Any = Map(
    "device_id" -> "xxx",
    "domain" -> "light",
    "entity_id" -> light.name,
    "type" -> "brightness_decrease",
  )

  override def variables: Set[Entity[_]] = Set(light)
