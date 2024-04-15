package com.v6ak.hagen.expressions

final case class AutomationEntity(name: String) extends SwitchableEntity:

  def lastTriggered: Expr[String] = StateAttr(this, "last_triggered")

  override def entityType: String = "automation"
