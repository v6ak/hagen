package com.v6ak.hagen.automation
import com.v6ak.hagen.expressions.{Context, Entity}

object BootTrigger extends Trigger[Nothing]:
  override def toStructure(context: Context): Any = Map(
    "platform" -> "homeassistant",
    "event" -> "start",
  )

  override def variables: Set[Entity[_]] = Set()

