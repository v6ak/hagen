package com.v6ak.hagen.dashboards

import com.v6ak.hagen.expressions.{Const, Context, Entity, Expr, StringType}

case class Markdown(content: Expr[String]) extends Card:
  override def toStructure(context: Context): Any = Map(
    "type" -> "markdown",
    "content" -> content.asCompleteJinja(context)
  )

  override def variables: Set[Entity[_]] = content.variables

object Markdown:
  def apply(content: String): Markdown = Markdown(Const(content))