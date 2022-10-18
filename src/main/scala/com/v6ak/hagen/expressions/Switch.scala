package com.v6ak.hagen.expressions

final case class Switch(name: String) extends Entity[Boolean] with SwitchableEntity {
  override def entityType: String = "switch"
}
