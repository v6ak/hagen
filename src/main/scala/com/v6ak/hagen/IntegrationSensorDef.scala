package com.v6ak.hagen

import com.v6ak.hagen.expressions.{Context, Entity, EnumType}
import com.v6ak.hagen.expressions.DoubleType

enum IntegrationMethod(val name: String):
  case Trapezoidal extends IntegrationMethod("trapezoidal")
  case Left extends IntegrationMethod("left")
  case Right extends IntegrationMethod("right")

class IntegrationSensorDef(
  name: String,
  id: String,
  unitPrefix: String,
  source: Entity[Double],
  method: IntegrationMethod = IntegrationMethod.Trapezoidal,
) extends Template[Double] {

  def sensorType: String = "sensor"

  def entity: Entity[Double] = Entity(haName(sensorType, name))

  override def toStructure(context: Context): Any = Map(
    "platform" -> "integration",
    "name" -> name,
    "unique_id" -> id,
    "unit_prefix" -> unitPrefix,
    "source" -> source.name,
    "method" -> method.name,
  )

  override def defined: Set[Entity[_]] = Set(entity)

  override def variables: Set[Entity[_]] = Set(source)

}
