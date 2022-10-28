package com.v6ak.hagen.expressions

import com.v6ak.hagen.{TupleElement, haName, optionalMap}

enum UtilityMeterCycle(override val toString: String):
  case QuarterHourly extends UtilityMeterCycle("quarter-hourly")
  case Hourly extends UtilityMeterCycle("hourly")
  case Daily extends UtilityMeterCycle("daily")
  case Weekly extends UtilityMeterCycle("weekly")
  case Monthly extends UtilityMeterCycle("monthly")
  case Bimonthly extends UtilityMeterCycle("bimonthly")
  case Quarterly extends UtilityMeterCycle("quarterly")
  case Yearly extends UtilityMeterCycle("yearly")

final case class UtilityMeterDef[T](
  name: String,
  source: Entity[T],
  cycle: UtilityMeterCycle,
  friendlyName: Option[String] = None,
)(implicit jinjaType: Type[T]) extends TupleElement:
  override def toStructure(context: Context): (String, Map[_, _]) = entity.baseName -> toInnerStructure(context)

  def toInnerStructure(context: Context): Map[_, _] = Map(
    "source" -> source.name,
    "cycle" -> cycle.toString,
  ) ++ optionalMap("name", friendlyName)

  override def variables: Set[Entity[_]] = Set(source)

  override def defined: Set[Entity[_]] = Set(entity)

  def entity = Entity[T](haName("sensor", name))
