package com.v6ak.hagen.actions

import com.v6ak.hagen
import com.v6ak.hagen.conditions.{Condition, TemplateCondition}
import com.v6ak.hagen.expressions.{Context, Entity, Expr}

type Conditions = Seq[Condition]
type Actions = Seq[Action]

@deprecated("Use ConditionalAction instead")
abstract class Choose extends Action{
  def branches: Seq[(Conditions, Actions)]
  def defaultOption: Option[Actions]

  override def toStructure(context: Context): Any = Map(
    "choose" -> branches.map{case (conditions, actions) =>
      Map(
        "conditions" -> conditions.map(_.toStructure(context)),
        "sequence" -> actions.map(_.toStructure(context))
      )
    }
  ) ++ hagen.optionalMap("default", defaultOption.map(_.map(_.toStructure(context))))

  override def variables: Set[Entity[_]] = branches.flatMap(branch =>
    branch._1.flatMap(_.variables) ++ branch._2.flatMap(_.variables)
  ).toSet
}

case class ChooseWithoutDefault(branches: Seq[(Conditions, Actions)]) extends Choose with ChooseBuilder {
  def when(conditions: Conditions, actions: Actions) = ChooseWithoutDefault(branches = branches ++ Seq((conditions, actions)))
  def default(actions: Actions) = ChooseWithDefault(branches = branches, defaultActions = actions)
  override def defaultOption: Option[Actions] = None
}

case class ChooseWithDefault[T](branches: Seq[(Conditions, Actions)], defaultActions: Actions) extends Choose {
  override def defaultOption: Option[Actions] = Some(defaultActions)
}

trait ChooseBuilder {
  def when(conditions: Conditions, actions: Actions): ChooseWithoutDefault
  def when(condition: Condition, actions: Actions): ChooseWithoutDefault =
    when(conditions = Seq(condition), actions = actions)
  def when(condition: Expr[Boolean], actions: Seq[Action]): ChooseWithoutDefault =
    when(conditions = Seq(TemplateCondition(condition)), actions = actions)
}

@deprecated("Use ConditionalAction instead")
object Choose extends ChooseBuilder{
  def when(conditions: Conditions, actions: Actions) = ChooseWithoutDefault(branches = Seq((conditions, actions)))
}