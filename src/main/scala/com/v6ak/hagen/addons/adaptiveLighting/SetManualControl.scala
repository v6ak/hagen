package com.v6ak.hagen.addons.adaptiveLighting

import com.v6ak.hagen.actions.Action
import com.v6ak.hagen.expressions.{Context, Entity, Light}

case class SetManualControl(status: Boolean, lights: Light*) extends Action {
  override def toStructure(context: Context): Any = Map(
    "service" -> "adaptive_lighting.set_manual_control",
    "data" -> Map(
      "entity_id" -> "switch.adaptive_lighting_default",
      "manual_control" -> status,
      "lights" -> lights.map(_.name)
    )
  )

  override def variables: Set[Entity[_]] = lights.toSet
}
