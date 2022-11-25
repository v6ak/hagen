package com.v6ak.hagen.automation

import com.v6ak.hagen.expressions.{Context, Entity}

final case class MqttTrigger(topic: String) extends Trigger[Nothing]:
  override def variables: Set[Entity[_]] = Set()

  override def toStructure(context: Context): Any = Map(
    "platform" -> "mqtt",
    "topic" -> topic,
  )

object MqttTrigger:
  def forZigbee2mqtt(id: String): MqttTrigger = MqttTrigger(s"zigbee2mqtt/$id")