package com.v6ak.hagen.dashboards

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.{Context, Entity}

class Navigate(link: String) extends ButtonAction:
  override def variables: Set[Entity[_]] = Set()
  override def toStructure(context: Context): Any = Map(
    "action" -> "navigate",
    "navigation_path" -> link,
  ) ++ confirmationMap

