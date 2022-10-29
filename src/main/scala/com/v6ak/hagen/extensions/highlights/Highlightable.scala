package com.v6ak.hagen.extensions.highlights

import com.v6ak.hagen.dashboards.Icon
import com.v6ak.hagen.{DelegatingElement, Element, optionalMap}
import com.v6ak.hagen.expressions.{Context, Expr}

final case class Highlightable(
  id: String,
  conditions: Seq[SimpleCondition[_]],
  priority: Double,
  link: Option[String], // TODO: stringly
  description: Expr[String],
  notification: Boolean,
  icon: Icon,
  group: Option[String] = None, // TODO: stringly
) extends DelegatingElement:
  override def toStructure(context: Context) = Map(
    "id" -> id,
    "conditions" -> conditions.map(_.toStructure(context)),
    "icon" -> icon,
    "priority" -> priority,
    "description" -> description.asCompleteJinja(context),
    "notify" -> notification,
  ) ++
    optionalMap("link", link) ++
    optionalMap("group", group)

  override def subElements: Set[Element] = conditions.toSet
