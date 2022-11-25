package com.v6ak.hagen.dashboards

import com.v6ak.hagen.ifNonDefault
import com.v6ak.hagen.dashboards.Glance.DefaultShowName
import com.v6ak.hagen.expressions.{Const, Context, Entity, Expr}
import com.v6ak.hagen.optionalMap

/**
 * https://www.home-assistant.io/dashboards/glance/
 */
case class Glance(
  title: Option[String] = None,
  showName: Boolean = DefaultShowName,
  showIcon: Boolean = DefaultShowIcon,

)(entities: (StatsEntity[_]|GlanceEntity[_])*) extends Card:
  import Glance._
  override def toStructure(context: Context): Any = Map(
    "type" -> "glance",
    "entities" -> entities.map(_.toStructure(context)),
  ) ++
    optionalMap("title", title) ++
    optionalMap("show_icon", ifNonDefault(showIcon, DefaultShowIcon)) ++
    optionalMap("show_name", ifNonDefault(showName, DefaultShowName))

  override def variables: Set[Entity[_]] = entities.flatMap(_.variables).toSet

object Glance:
  val DefaultShowName = true
  val DefaultShowIcon = true