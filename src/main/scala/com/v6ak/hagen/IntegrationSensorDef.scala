package com.v6ak.hagen

import com.v6ak.hagen.expressions.{Context, Entity, EnumType}
import com.v6ak.hagen.expressions.DoubleType

class IntegrationSensorDef(name: String, id: String, unitPrefix: String, source: Entity[Double]) extends Template[Double] {

  def sensorType: String = "sensor"

  def entity: Entity[Double] = Entity(haName(sensorType, name))

  override def toStructure(context: Context): Any = Map(
    "platform" -> "integration",
    "name" -> name,
    "unique_id" -> id,
    "unit_prefix" -> unitPrefix,
    "source" -> source.name,
  )

  override def defined: Set[Entity[_]] = Set(entity)

  override def variables: Set[Entity[_]] = Set(source)

}
