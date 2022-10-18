package com.v6ak.hagen.expressions

final case class InputBoolean(name: String) extends Entity[Boolean] with SwitchableEntity {
  override def entityType: String = "input_boolean"
}
