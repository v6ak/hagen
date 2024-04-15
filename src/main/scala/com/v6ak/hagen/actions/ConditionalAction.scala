package com.v6ak.hagen.actions

import com.v6ak.hagen
import com.v6ak.hagen.conditions.{Condition, TemplateCondition}
import com.v6ak.hagen.expressions.{Context, Entity, Expr}

import scala.annotation.targetName

abstract class ConditionalAction extends Action:
  def branches: Seq[(Seq[Condition], Seq[Action])]

  def defaultBranch: Seq[Action]

  override def toStructure(context: Context): Any = branches match {
    case Seq((conditions, ifTrue)) =>
      Map(
        "if" -> conditions.map(_.toStructure(context)),
        "then" -> ifTrue.map(_.toStructure(context)),
      ) ++ hagen.mapIfNonEmpty("else", defaultBranch.map(_.toStructure(context)))
    case generic =>
      Map(
        "choose" -> branches.map((conditions, actions) =>
          Map(
            "conditions" -> conditions.map(_.toStructure(context)),
            "sequence" -> actions.map(_.toStructure(context)),
          )
        )
      ) ++ hagen.mapIfNonEmpty("default", defaultBranch.map(_.toStructure(context)))

  }

  private def branchesVariables = branches.flatMap((conditions, actions) =>
    conditions.flatMap(_.variables) ++ actions.flatMap(_.variables)
  )

  override def variables: Set[Entity[_]] = branchesVariables.toSet ++ defaultBranch.flatMap(_.variables)


case class FullConditionalAction(branches: Seq[(Seq[Condition], Seq[Action])], defaultBranch: Seq[Action]) extends ConditionalAction

trait ConditionalActionBuilder:
  def branches: Seq[(Seq[Condition], Seq[Action])]
  @targetName("whenSeq")
  def when(conditions: Seq[Condition]): ConditionalActionFactory =
    ConditionalActionFactory(conditions, branches = branches)
  def when(conditions: Condition*): ConditionalActionFactory =
    ConditionalActionFactory(conditions, branches = branches)
  def when(condition: Condition): ConditionalActionFactory =
    ConditionalActionFactory(Seq(condition), branches = branches)
  def when(condition: Expr[Boolean]): ConditionalActionFactory =
    ConditionalActionFactory(Seq(TemplateCondition(condition)), branches = branches)

case class ConditionalActionWithoutDefault(branches: Seq[(Seq[Condition], Seq[Action])]) extends ConditionalAction with ConditionalActionBuilder:
  override def defaultBranch: Seq[Nothing] = Seq()

  def ifFalse(actions: Action*): FullConditionalAction = FullConditionalAction(branches, defaultBranch = actions)
  @targetName("ifFalseSeq")
  def ifFalse(actions: Seq[Action]): FullConditionalAction = FullConditionalAction(branches, defaultBranch = actions)

  def ifNoBranchMatches(actions: Action*): FullConditionalAction = FullConditionalAction(branches, defaultBranch = actions)
  @targetName("ifNoBranchMatchesSeq")
  def ifNoBranchMatches(actions: Seq[Action]): FullConditionalAction = FullConditionalAction(branches, defaultBranch = actions)

  def elseDo(actions: Action*): FullConditionalAction = FullConditionalAction(branches, defaultBranch = actions)
  @targetName("elseDoSeq")
  def elseDo(actions: Seq[Action]): FullConditionalAction = FullConditionalAction(branches, defaultBranch = actions)


class ConditionalActionFactory(conditions: Seq[Condition], branches: Seq[(Seq[Condition], Seq[Action])]):
  def doActions(actions: Action*): ConditionalActionWithoutDefault = ConditionalActionWithoutDefault(branches ++ Seq((conditions, actions)))
  @targetName("doActionsSeq")
  def doActions(actions: Seq[Action]): ConditionalActionWithoutDefault = doActions(actions: _*)

object ConditionalAction extends ConditionalActionBuilder:
  override def branches: Seq[(Seq[Condition], Seq[Action])] = Seq()

  // for compatibility with old API
  def apply(
    conditions: Seq[Condition],
    ifTrue: Seq[Action],
    ifFalse: Seq[Action] = Seq(),
  ) =
    when(conditions)
      .doActions(ifTrue)
      .ifFalse(ifFalse)

  @deprecated("use ConditionalAction.when(…)")
  def apply(condition: Condition, ifTrue: Seq[Action], ifFalse: Seq[Action]): ConditionalAction = ConditionalAction(
    conditions = Seq(condition),
    ifTrue = ifTrue,
    ifFalse = ifFalse,
  )

  @deprecated("use ConditionalAction.when(…)")
  def apply(condition: Condition, ifTrue: Seq[Action]): ConditionalAction = ConditionalAction(
    conditions = Seq(condition),
    ifTrue = ifTrue,
  )

  @deprecated("use ConditionalAction.when(…)")
  def apply(condition: Expr[Boolean], ifTrue: Seq[Action], ifFalse: Seq[Action]): ConditionalAction = ConditionalAction(
    conditions = Seq(TemplateCondition(condition)),
    ifTrue = ifTrue,
    ifFalse = ifFalse,
  )

  @deprecated("use ConditionalAction.when(…)")
  def apply(condition: Expr[Boolean], ifTrue: Seq[Action]): ConditionalAction = ConditionalAction(
    conditions = Seq(TemplateCondition(condition)),
    ifTrue = ifTrue,
  )
