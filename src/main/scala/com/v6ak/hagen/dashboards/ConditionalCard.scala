package com.v6ak.hagen.dashboards

import com.v6ak.hagen.dashboards.Card
import com.v6ak.hagen.expressions.{Context, Entity}
import com.v6ak.hagen.extensions.highlights.SimpleCondition

case class ConditionalCard(conditions: Seq[SimpleCondition[_]], card: Card) extends Card:
  override def toStructure(context: Context): Any = Map(
    "type" -> "conditional",
    "conditions" -> conditions.map(_.toStructure(context)),
    "card" -> card.toStructure(context)
  )

  override def variables: Set[Entity[_]] = conditions.flatMap(_.variables).toSet ++ card.variables
