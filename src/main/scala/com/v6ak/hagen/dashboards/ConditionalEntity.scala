package com.v6ak.hagen.dashboards

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.{Context, Entity}
import com.v6ak.hagen.extensions.highlights.SimpleCondition

// https://www.home-assistant.io/dashboards/entities/
case class ConditionalEntity[T](
  conditions: Seq[SimpleCondition[_]],
  row: StatsEntity[T]|ButtonEntity[T],
) extends Element:
  override def toStructure(context: Context): Any = Map(
    "type" -> "conditional",
    "conditions" -> conditions.map(_.toStructure(context)),
    "row" -> row.toStructure(context),
  )

  override def variables: Set[Entity[_]] = conditions.flatMap(_.variables).toSet ++ row.variables

  override def defined: Set[Entity[_]] = Set()

