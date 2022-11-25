package com.v6ak.hagen.automation

import com.v6ak.hagen.actions.Action
import com.v6ak.hagen.conditions.{Condition, TemplateCondition}
import com.v6ak.hagen.expressions.unsafe.VarExpr
import com.v6ak.hagen.expressions.{Context, Entity, Expr}
import com.v6ak.hagen.hardware.ikea.styrbar.StyrbarAction

import scala.annotation.targetName

final case class AutomationBase(
  conditions: Seq[Condition],
  triggers: Seq[Trigger[_]],
  actions: Seq[Action],
  mode: ScriptMode = ScriptMode.Single,
):

  def toStructureFragment(context: Context): Map[String, Any] = Map(
    "mode" -> mode.name,
    "trigger" -> triggers.map(_.toStructure(context)),
    "condition" -> conditions.map(_.toStructure(context)),
    "action" -> actions.map(_.toStructure(context)),
  )

  def variables: Set[Entity[_]] = (
    conditions.flatMap(_.variables) ++ triggers.flatMap(_.variables) ++ actions.flatMap(_.variables)
  ).toSet

  def toAutomation(id: String, alias: String): Automation = Automation(id = id, alias = alias, base = this)

class AutomationBaseBuilderStage1(mode: ScriptMode):
  def on[T](triggers: Trigger[T]*): AutomationBaseBuilderStage2[T] = AutomationBaseBuilderStage2(mode, triggers)


private def TriggerExpr[T] = VarExpr[T]("""trigger""", Set())


class AutomationBaseBuilderStage2[T](mode: ScriptMode, triggers: Seq[Trigger[T]]):
  def providedThat(conditions: Condition*): AutomationBaseBuilderStage3[T] = AutomationBaseBuilderStage3(mode, triggers, conditions)
  def providedThat(condition: Expr[Boolean]): AutomationBaseBuilderStage3[T] = AutomationBaseBuilderStage3(mode, triggers, Seq(TemplateCondition(condition)))

  def providedThat(conditions: Expr[T] => Seq[Condition]): AutomationBaseBuilderStage3[T] = AutomationBaseBuilderStage3(mode, triggers, conditions(TriggerExpr[T]))
  @targetName("providedThatSingleCondition")
  def providedThat(conditions: Expr[T] => Condition): AutomationBaseBuilderStage3[T] = AutomationBaseBuilderStage3(mode, triggers, Seq(conditions(TriggerExpr[T])))
  @targetName("providedThatSingleTemplateCondition")
  def providedThat(conditions: Expr[T] => Expr[Boolean]): AutomationBaseBuilderStage3[T] = AutomationBaseBuilderStage3(mode, triggers, Seq(TemplateCondition(conditions(TriggerExpr[T]))))

class AutomationBaseBuilderStage3[T](mode: ScriptMode, triggers: Seq[Trigger[T]], conditions: Seq[Condition]):
  def doActions(actions: Action*): AutomationBase = AutomationBase(conditions, triggers, actions, mode)

  def doActions(f: Expr[T] => Seq[Action]): AutomationBase = AutomationBase(conditions, triggers, f(TriggerExpr[T]), mode)

  def doAction(f: Expr[T] => Action): AutomationBase = AutomationBase(conditions, triggers, Seq(f(TriggerExpr[T])), mode)

object AutomationBase extends AutomationBaseBuilderStage1(ScriptMode.Single):
  def useMode(mode: ScriptMode): AutomationBaseBuilderStage1 = AutomationBaseBuilderStage1(mode)
