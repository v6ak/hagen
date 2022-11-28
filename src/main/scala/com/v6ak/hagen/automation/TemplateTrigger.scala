package com.v6ak.hagen.automation

import com.v6ak.hagen.expressions.{Context, Entity, Expr}

final case class TemplateTrigger(condition: Expr[Boolean]) extends Trigger[Nothing] :
  override def toStructure(context: Context): Any = Map(
    "platform" -> "template",
    "value_template" -> condition.toStructure(context),
  )

  override def variables: Set[Entity[_]] = condition.variables
