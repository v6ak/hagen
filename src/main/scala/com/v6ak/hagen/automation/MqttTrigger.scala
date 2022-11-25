package com.v6ak.hagen.automation

import com.v6ak.hagen.expressions.*

trait MqttTriggerDescription{}

final implicit class MqttTriggerDescriptionOps(e: Expr[MqttTriggerDescription]) extends AnyVal:
  def payloadJson: Expr[JsonObject] = unsafe.FieldExpr(e, "payload_json")

final case class MqttTrigger(topic: String) extends Trigger[MqttTriggerDescription]:
  override def variables: Set[Entity[_]] = Set()

  override def toStructure(context: Context): Any = Map(
    "platform" -> "mqtt",
    "topic" -> topic,
  )

object MqttTrigger:
  def forZigbee2mqtt(id: String): MqttTrigger = MqttTrigger(s"zigbee2mqtt/$id")