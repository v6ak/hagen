package com.v6ak.hagen.examples

import com.v6ak.hagen.extensions.fridge.FridgeModule
import DefinedItems.*
import com.v6ak.hagen.expressions.Const
import com.v6ak.hagen.expressions.DoubleType

val Fridge = FridgeModule(
  fridgeHumidity = fridgeHumiditySensor,
  fridgeTemperature = fridgeTemperatureSensor,
  averageCoolingPower = Const(60.0), // the fridge takes 60W on average when cooling
  averageStandbyPower = Const(0.0), // the fridge takes negligible power when not cooling
)
