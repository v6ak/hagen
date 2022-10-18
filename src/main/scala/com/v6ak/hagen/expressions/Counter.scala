package com.v6ak.hagen.expressions

import com.v6ak.hagen.actions.{Action, ServiceCall}

case class Counter(name: String) extends Entity[Int]:
  def increment(): Action = ServiceCall("counter.increment", this)
  def decrement(): Action = ServiceCall("counter.decrement", this)
  def reset(): Action = ServiceCall("counter.reset", this)
