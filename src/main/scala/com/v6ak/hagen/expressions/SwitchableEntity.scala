package com.v6ak.hagen.expressions

import com.v6ak.hagen.actions.{Action, ConditionalAction, ServiceCall}
import com.v6ak.hagen.conditions.TemplateCondition

trait SwitchableEntity extends Entity[Boolean]:
  def entityType: String

  def turn(state: Boolean): Action = if state then turnOn() else turnOff()

  def turnOn(): Action = ServiceCall(s"$entityType.turn_on", this)

  def turnOff(): Action = ServiceCall(s"$entityType.turn_off", this)

  def turnOffIfOn(): Action =
    ConditionalAction.when(this)
      .doActions(turnOff())

  def turnOnIfOff(): Action =
    ConditionalAction.when(!this)
      .doActions(turnOn())

  def toggle(): Action = ServiceCall(s"$entityType.toggle", this)
