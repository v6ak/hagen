package com.v6ak.hagen.expressions

import com.v6ak.hagen.automation.Automation

/**
 * This is half-done and I am not sure whether to finish it or to remove it. The idea was to make self-referencing
 * in automation possible, so you can see when it started. This would be useful for non-linear progress of brigtness.
 * However, this affects very much of the code, and I am not sure it is worth it.
 */
final case class Context private (/*currentAutomation: Option[Automation] = None*/notYetDone: Boolean = true)

object Context {
  val Empty = Context()
  /**
   * Put it where you don't know what Context to use.
   * TODO: remove it
   */
  @deprecated
  val TemporaryHack = Empty
}