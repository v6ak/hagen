package com.v6ak.hagen.dashboards

import com.v6ak.hagen.materializeElements
import com.v6ak.hagen.dashboards.Card
import com.v6ak.hagen.expressions.{Context, Entity}

case class RawCard(structure: Map[String, Any], override val variables: Set[Entity[_]]) extends Card:
  override def toStructure(context: Context): Any = materializeElements(context, structure)
