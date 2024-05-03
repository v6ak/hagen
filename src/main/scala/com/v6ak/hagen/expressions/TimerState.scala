package com.v6ak.hagen.expressions

enum TimerState(val name: String):
  case Idle extends TimerState("idle")
  case Active extends TimerState("active")
  case Paused extends TimerState("paused")

  override def toString: String = name
