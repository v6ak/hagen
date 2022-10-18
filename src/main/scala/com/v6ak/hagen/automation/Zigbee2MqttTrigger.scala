package com.v6ak.hagen.automation

import com.v6ak.hagen.expressions.{Context, Entity}

final case class Zigbee2MqttTrigger(topic: String) extends Trigger:
  override def variables: Set[Entity[_]] = Set()

  override def toStructure(context: Context): Any = Map(
    "platform" -> "mqtt",
    "topic" -> topic,
  )
