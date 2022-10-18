package com.v6ak.hagen.conditions

import com.v6ak.hagen.conditions
import com.v6ak.hagen.expressions.{Context, Entity, Expr}

final case class TemplateCondition(condition: Expr[Boolean]) extends Condition:
  def toStructure(context: Context) = Map(
    "condition" -> "template",
    "value_template" -> condition.asCompleteJinja(context),
  )

  override def variables: Set[Entity[_]] = condition.variables
