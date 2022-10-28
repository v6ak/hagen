package com.v6ak.hagen.automation

import com.v6ak.hagen.{Element, NakeableElement, haName}
import com.v6ak.hagen.actions.Action
import com.v6ak.hagen.conditions.Condition
import com.v6ak.hagen.expressions.{Context, Entity, Switch}


final case class Automation(
  id: String,
  alias: String,
  conditions: Seq[Condition],
  triggers: Seq[Trigger],
  actions: Seq[Action],
  mode: ScriptMode = ScriptMode.Single
) extends NakeableElement:
  def toStructure(context: Context): Any = Map(
    s"automation $id" -> toInnerStructure(context)
  )

  def toInnerStructure(context: Context): Any = {
    val newContext = context.copy(currentAutomation = Some(entity))
    Map(
      "id" -> id,
      "alias" -> alias,
      "mode" -> mode.name,
      "trigger" -> triggers.map(_.toStructure(newContext)),
      "condition" -> conditions.map(_.toStructure(newContext)),
      "action" -> actions.map(_.toStructure(newContext)),
    )
  }

  override def variables: Set[Entity[_]] = (
    conditions.flatMap(_.variables) ++ triggers.flatMap(_.variables) ++ actions.flatMap(_.variables)
  ).toSet

  override def defined: Set[Entity[_]] = Set(entity)

  def entity = Switch(haName("automation", alias))
