package com.v6ak.hagen.dashboards
import com.v6ak.hagen.dashboards.HistoryGraph.{DefaultHoursToShow, DefaultRefreshInterval}
import com.v6ak.hagen.optionalMap
import com.v6ak.hagen.ifNonDefault
import com.v6ak.hagen.expressions.{Const, Context, Entity, Expr}

/**
 * https://www.home-assistant.io/dashboards/history-graph/
 */
case class HistoryGraph(
  title: Option[String] = None,
  hoursToShow: Int = DefaultHoursToShow,
  refreshInterval: Int = DefaultRefreshInterval,
)(entities: StatsEntity[_]*) extends Card:
  override def toStructure(context: Context): Any = Map(
    "type" -> "history-graph",
    "entities" -> entities.map(_.toStructure(context)),
  ) ++
    optionalMap("title", title) ++
    optionalMap("hours_to_show", ifNonDefault(hoursToShow, DefaultHoursToShow)) ++
    optionalMap("refresh_interval", ifNonDefault(refreshInterval, DefaultRefreshInterval))
  

  override def variables: Set[Entity[_]] = entities.flatMap(_.variables).toSet


object HistoryGraph:
  import com.v6ak.hagen.expressions.IntType

  val DefaultHoursToShow = 24

  val DefaultRefreshInterval = 0