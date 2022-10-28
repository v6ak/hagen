package com.v6ak.hagen.dashboards

import com.v6ak.hagen.expressions.{Const, Context, Entity, Expr}
import com.v6ak.hagen.optionalMap

/**
 * https://www.home-assistant.io/dashboards/entities/
 */
case class Entities(
  title: Option[String] = None,
)(entities: StatsEntity[_]*) extends Card:
  override def toStructure(context: Context): Any = Map(
    "type" -> "entities",
    "entities" -> entities.map(_.toStructure(context)),
  ) ++
    optionalMap("title", title)

  override def variables: Set[Entity[_]] = entities.flatMap(_.variables).toSet
