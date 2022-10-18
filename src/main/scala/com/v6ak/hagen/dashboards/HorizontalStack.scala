package com.v6ak.hagen.dashboards

import com.v6ak.hagen.dashboards.Card
import com.v6ak.hagen.expressions.{Context, Entity}

case class HorizontalStack(cards: Card*) extends Card:
  override def toStructure(context: Context): Any = Map(
    "type" -> "horizontal-stack",
    "cards" -> cards.map(_.toStructure(context))
  )

  override def variables: Set[Entity[_]] = cards.flatMap(_.variables).toSet
