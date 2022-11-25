package com.v6ak.hagen.extensions.batteryPowered

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.{Context, Entity}
import com.v6ak.hagen.expressions.DoubleType

final case class BatteryPowered(id: String, name: String, deviceType: String, criticalBattery: Double):

  def percentage: Entity[Double] = Entity[Double](s"sensor.${id}_battery")
