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
)