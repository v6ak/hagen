package com.v6ak.hagen

import com.v6ak.hagen.expressions.{Context, Entity, Type}

final case class HistoryStatsDef[S, T](
  name: String,
  source: Entity[S],
  sourceStates: Seq[S],
  statsType: HistoryStatsType[T],
  period: Period,
)(implicit sourceJinjaType: Type[S]) extends GenericSensorDef[T]:
  private implicit def outputJinjaType: Type[T] = statsType.jinjaType

  override def entity: Entity[T] = Entity[T](haName("sensor", name))

  override def variables: Set[Entity[_]] = source.variables ++ period.variables

  override def toStructure(context: Context): Any = {
    val sourceStatesDef = sourceStates.map(sourceJinjaType.serialize) match
      case Seq(single) => single
      case other => other
    Map(
      "name" -> name,
      "platform" -> "history_stats",
      "entity_id" -> source.name,
      "state" -> sourceStatesDef,
      "type" -> statsType.name,
    ) ++ period.toStructure(context)
  }

  override def defined: Set[Entity[_]] = Set(entity)
