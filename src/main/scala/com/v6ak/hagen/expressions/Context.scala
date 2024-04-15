package com.v6ak.hagen.expressions

import com.v6ak.hagen.automation.Automation

final case class Context (
  notYetDone: Boolean = true,
  currentAutomation: Option[SwitchableEntity] = None
)

object Context {
  val Empty = Context()
  /**
   * Put it where you don't know what Context to use.
   * TODO: remove it
   */
  @deprecated("Just for transition")
  val TemporaryHack = Empty
}