package com.v6ak.hagen.actions

import com.v6ak.hagen.conditions.Condition
import com.v6ak.hagen.conditions.TemplateCondition
import com.v6ak.hagen.expressions.{Context, Entity, Expr}

final case class While(conditions: Seq[Condition], sequence: Seq[Action]) extends Action:
  override def toStructure(context: Context): Any = Map(
    "repeat" -> Map(
      "while" -> (conditions match {
        case Seq(TemplateCondition(cond)) => cond.asCompleteJinja(context)
        case other => other.map(_.toStructure(context))
      }),
      "sequence" -> sequence.map(_.toStructure(context)),
    )
  )

  override def variables: Set[Entity[_]] = (conditions.flatMap(_.variables) ++ sequence.flatMap(_.variables)).toSet


object While:

  def apply(condition: Expr[Boolean], sequence: Seq[Action]) = new While(
    Seq(TemplateCondition(condition)),
    sequence
  )