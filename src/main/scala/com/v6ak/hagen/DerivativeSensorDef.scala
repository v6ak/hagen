package com.v6ak.hagen

import com.v6ak.hagen.expressions.{Context, DoubleType, Entity}
import com.v6ak.hagen.{Template, haName}

final case class DerivativeSensorDef(
  name: String,
  // id: String,
  unitTime: String,
  // unitPrefix: String,
  source: Entity[Double],
  unit: Option[String] = None,
) extends Template[Double] {

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

  override def defined: Set[Entity[_]] = Set(entity)

  override def variables: Set[Entity[_]] = Set(source)

}
