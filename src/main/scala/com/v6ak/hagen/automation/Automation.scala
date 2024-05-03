package com.v6ak.hagen.automation

import com.v6ak.hagen.{Element, EntityDef, NakeableElement, haName}
import com.v6ak.hagen.actions.Action
import com.v6ak.hagen.conditions.Condition
import com.v6ak.hagen.expressions.{AutomationEntity, Context, Entity, Switch}
import com.v6ak.hagen.output.{Automations, HagenKey}


final case class Automation(
  id: String,
  alias: String,
  base: AutomationBase,
) extends NakeableElement with EntityDef[AutomationEntity, Seq[Automation]]:
  def toStructure(context: Context): Any = Map(
    s"automation $id" -> toInnerStructure(context)
  )

  override def key: HagenKey[Seq[Automation]] = Automations
  override def createHagenDefinition: Seq[Automation] = Seq(this)

  def toInnerStructure(context: Context): Any = {
    val newContext = context.copy(currentAutomation = Some(entity))
    Map(
      "id" -> id,
      "alias" -> alias,
    ) ++ base.toStructureFragment(newContext)
  }

  override def variables: Set[Entity[_]] = base.variables

  override def defined: Set[Entity[_]] = Set(entity)

  def entity = AutomationEntity(haName("automation", alias))


object Automation:
  def apply(
    id: String,
    alias: String,
    conditions: Seq[Condition],
    triggers: Seq[Trigger[_]],
    actions: Seq[Action],
    mode: ScriptMode = ScriptMode.Single
  ): Automation = Automation(
    id = id,
    alias = alias,
    base = AutomationBase(
      conditions = conditions,
      triggers = triggers,
      actions = actions,
      mode = mode,
    )
  )
