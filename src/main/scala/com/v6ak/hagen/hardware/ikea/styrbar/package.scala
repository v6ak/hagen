package com.v6ak.hagen.hardware.ikea

import com.v6ak.hagen.automation.{Change, MqttTrigger}
import com.v6ak.hagen.automation.MqttTriggerDescription
import com.v6ak.hagen.automation.MqttTriggerDescriptionOps
import com.v6ak.hagen.expressions
import com.v6ak.hagen.expressions.*
import com.v6ak.hagen.extensions.brightnessButtons.BrighnessAutomationSource
import com.v6ak.hagen.hardware.ikea.styrbar.StyrbarAction


package object styrbar:

  val mqttBrightnessActionsSource: BrighnessAutomationSource[String, StyrbarAction, MqttTriggerDescription] =
    BrighnessAutomationSource[String, StyrbarAction, MqttTriggerDescription](
      triggerGen = MqttTrigger.forZigbee2mqtt,
      triggerToButton = triggerDescr => expressions.unsafe.Reinterpret(triggerDescr.payloadJson.getUnsafe("action")),
      brightnessDownButton = Const(StyrbarAction.BrightnessMoveDown),
      brightnessUpButton = Const(StyrbarAction.BrightnessMoveUp),
      brightnessStopButtons = Seq(Const(StyrbarAction.BrightnessStop)),
    )
