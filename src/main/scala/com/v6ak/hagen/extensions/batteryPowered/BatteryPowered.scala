package com.v6ak.hagen.extensions.batteryPowered

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.{Context, Entity}

final case class BatteryPowered(id: String, name: String, deviceType: String, criticalBattery: Double) extends Element {
  override def toStructure(context: Context): Any = Map(
    "entity_id" -> id,
    "id" -> id,
    "type" -> deviceType,
    "name" -> name,
    "low_battery_threshold" -> criticalBattery,
  )

  override def variables: Set[Entity[_]] = Set() // TODO

  override def defined: Set[Entity[_]] = Set()
}
