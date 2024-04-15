package com.v6ak.hagen.actions

import com.v6ak.hagen.expressions.{Context, Entity, Expr}

final case class Delay(
  delay: Expr[Int]
) extends Action:

  override def toStructure(context: Context): Any = Map(
    "delay" -> delay.toStructure(context),
  )

  override def variables: Set[Entity[_]] = delay.variables
