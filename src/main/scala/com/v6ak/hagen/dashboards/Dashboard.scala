package com.v6ak.hagen.dashboards

import com.v6ak.hagen.{DelegatingElement, Element, RawElement}
import com.v6ak.hagen.expressions.{Context, Entity}
import com.v6ak.hagen.output.StringElement

final case class Dashboard(
  name: String,
  title: String,
  icon: Icon,
  showInSidebar: Boolean,
)(views: DashboardPage*) extends DelegatingElement:
  override def subElements: Set[Element] = views.toSet
  override def toStructure(context: Context): Any = Map(
    "views" -> views.map(_.toStructure(context)),
    "title" -> title,
  )
  def toMetaStructure: Map[String, Element] = Map(
    "name" -> StringElement(name),
    "title" -> StringElement(title),
    "icon" -> icon,
    "show_in_sidebar" -> RawElement(showInSidebar, variables = Set(), defined = Set()),
  )
