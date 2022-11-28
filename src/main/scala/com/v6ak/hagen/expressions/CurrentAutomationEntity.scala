package com.v6ak.hagen.expressions

// TODO: find a better type
object CurrentAutomationEntity extends ContextDependentEntity[String] with NonTransformable[String] :
  val LastTriggered: StateAttr[_] = StateAttr(this, "last_triggered")

  override def getName(context: Context): String = context.currentAutomation.get.name

  override def variables: Set[Entity[_]] = Set()
