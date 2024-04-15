package com.v6ak.hagen

import com.v6ak.hagen.expressions.{Context, DoubleType, Entity, Type}
import com.v6ak.hagen.haName

import scala.concurrent.duration.Duration

final case class DerivativeSensorDef[T <: Double|Int](
  name: String,
  // id: String,
  unitTime: String,
  // unitPrefix: String,
  source: Entity[T],
  unit: Option[String] = None,
  timeWindow: Option[Duration] = None,
)(
  implicit jinjaType: Type[T],
) extends GenericSensorDef[Double] {

  def sensorType: String = "sensor"

  def entity: Entity[Double] = Entity(haName(sensorType, name))

  override def toStructure(context: Context): Any = Map(
    "platform" -> "derivative",
    "name" -> name,
    //"unique_id" -> id,
    "unit_time" -> unitTime,
    //"unit_prefix" -> unitPrefix,
    "source" -> source.name,
  ) ++ optionalMap("unit", unit)
  ++ optionalMap("time_window", timeWindow.map(durationToMap))

  override def defined: Set[Entity[_]] = Set(entity)

  override def variables: Set[Entity[_]] = Set(source)

}
