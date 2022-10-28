package com.v6ak.hagen.dashboards
import com.v6ak.hagen.expressions.{Context, Entity}

case class Grid(square: Boolean, columns: Int)(cards: Card*) extends Card:
  override def toStructure(context: Context): Any = Map(
    "type" -> "grid",
    "square" -> square,
    "columns" -> columns,
    "cards" -> cards.map(_.toStructure(context)),
  )

  override def variables: Set[Entity[_]] = cards.flatMap(_.variables).toSet

