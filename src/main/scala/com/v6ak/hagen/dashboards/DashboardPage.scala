package com.v6ak.hagen.dashboards

import com.v6ak.hagen.{Element, optionalMap, ifNonDefault}
import com.v6ak.hagen.expressions.{Context, Entity}

enum DashboardPageType(override val toString: String):
  case Masonry extends DashboardPageType("masonry")
  case Sidebar extends DashboardPageType("sidebar")
  case Panel extends DashboardPageType("panel")

/**
 * https://www.home-assistant.io/dashboards/dashboards/
 */
case class DashboardPage(
  title: String,
  path: String,
  pageType: DashboardPageType = DashboardPageType.Sidebar,
  icon: Icon,
  theme: String = "Backend-selected",
  subview: Boolean = false
)(
  cards: Card*
) extends Element:
  override def toStructure(context: Context): Any = Map(
    "theme" -> theme,
    "title" -> title,
    "path" -> path,
    "type" -> pageType.toString,
    "icon" -> icon.toStructure,
    "badges" -> Seq(),
    "cards" -> cards.map(_.toStructure(context))
  ) ++ optionalMap("subview", ifNonDefault(subview, false))

  override def variables: Set[Entity[_]] = cards.flatMap(_.variables).toSet

  override def defined: Set[Entity[_]] = Set()

