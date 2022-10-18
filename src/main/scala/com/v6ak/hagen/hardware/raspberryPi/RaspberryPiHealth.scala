package com.v6ak.hagen.hardware.raspberryPi

import com.v6ak.HeteroMap
import com.v6ak.hagen.expressions.{BooleanType, Const, Entity, StringType}
import com.v6ak.hagen.extensions.highlights.{Highlightable, Highlightables, SimplePositiveCondition}
import com.v6ak.hagen.output.{HagenKey, SimpleHagenModule}

class RaspberryPiHealth(rpiPowerStatus: Entity[Boolean]) extends SimpleHagenModule :
  override def content: HeteroMap[HagenKey[_]] = HeteroMap(
    Highlightables -> Seq(
      Highlightable(
        group = Some("Power status issue"),
        id = "binary_sensor.rpi_power_status",
        conditions = Seq(SimplePositiveCondition(rpiPowerStatus, true)),
        priority = 0,
        description = Const("Raspberry Pi power issue!"),
        icon = "mdi:home-alert-outline",
        link = Some("/lovelace-home/system"),
        notification = true,
      ),
    ),
  )
