package com.v6ak.hagen.dashboards

import com.v6ak.hagen.expressions.{Context, Entity}

object MoreInfo extends ButtonAction:
  override def toStructure(context: Context): Any = Map("action" -> "more-info")

  override def variables: Set[Entity[_]] = Set()
