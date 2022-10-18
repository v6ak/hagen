package com.v6ak.hagen.actions

import com.v6ak.hagen
import com.v6ak.hagen.conditions.{Condition, TemplateCondition}
import com.v6ak.hagen.expressions.{Context, Entity, Expr}

final case class ConditionalAction(
  conditions: Seq[Condition],
  ifTrue: Seq[Action],
  ifFalse: Seq[Action] = Seq(),
) extends Action:
  override def toStructure(context: Context): Any = Map(
    "if" -> conditions.map(_.toStructure(context)),
    "then" -> ifTrue.map(_.toStructure(context)),
  ) ++ hagen.mapIfNonEmpty("else", ifFalse.map(_.toStructure(context)))

  override def variables: Set[Entity[_]] =
    (
      conditions.flatMap(_.variables) ++
        ifTrue.flatMap(_.variables) ++
        ifFalse.flatMap(_.variables)
    ).toSet

object ConditionalAction:

  def apply(condition: Condition, ifTrue: Seq[Action], ifFalse: Seq[Action]): ConditionalAction = ConditionalAction(
    conditions = Seq(condition),
    ifTrue = ifTrue,
    ifFalse = ifFalse,
  )

  def apply(condition: Condition, ifTrue: Seq[Action]): ConditionalAction = ConditionalAction(
    conditions = Seq(condition),
    ifTrue = ifTrue,
  )

  def apply(condition: Expr[Boolean], ifTrue: Seq[Action], ifFalse: Seq[Action]): ConditionalAction = ConditionalAction(
    conditions = Seq(TemplateCondition(condition)),
    ifTrue = ifTrue,
    ifFalse = ifFalse,
  )

  def apply(condition: Expr[Boolean], ifTrue: Seq[Action]): ConditionalAction = ConditionalAction(
    conditions = Seq(TemplateCondition(condition)),
    ifTrue = ifTrue,
  )
