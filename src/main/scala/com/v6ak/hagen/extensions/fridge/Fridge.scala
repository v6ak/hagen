package com.v6ak.hagen.extensions.fridge

import com.v6ak.HeteroMap
import com.v6ak.hagen.*
import com.v6ak.hagen.actions.ServiceCall
import com.v6ak.hagen.automation.{Automation, Change}
import com.v6ak.hagen.expressions.*
import com.v6ak.hagen.expressions.BooleanOps.*
import com.v6ak.hagen.expressions.DoubleOps.*
import com.v6ak.hagen.extensions.highlights.{Highlightable, Highlightables, SimplePositiveCondition}
import com.v6ak.hagen.extensions.hysteresis.*
import com.v6ak.hagen.output.*

case class FridgeEntities(
  humidity: Entity[Double],
  temperature: Entity[Double],
  humidityOk: Entity[Boolean],
  humidityOkRatio: Entity[Double],
  // hooHumid: Entity[Boolean],
  // tooHot: Entity[Boolean],
  doorOpen: Entity[Boolean],
  doorOpenCounter: Counter,
  estimatedPower: Entity[Double],
  estimatedEnergy: Entity[Double],
)

/**
 * Based on temperature+humidity sensor, it computes various useful metrics for the fridge:
 *
 * <ul>
 * <li>Warning for continuously high humidity. (It uses 24h periods, so it will not bother you just for high humidity
 *    after every opening the door).
 * <li>Warning for too high temperature
 * <li>Power consumption estimate. When temperature is decreasing, it assumes the fridge is cooling. Otherwise, it
 *    assumes the fridge to be in standby mode.
 * <li>Door open sensor + door open count. (Based on sudden humidity increase.)
 * </ul>
 *
 *
 * @param fridgeHumidity
 * @param fridgeTemperature
 * @param averageCoolingPower
 * @param averageStandbyPower
 * @param entityNamePrefix
 * @param humidityThreshold
 * @param tooHumidRatioPercentageLow
 * @param tooHumidRatioPercentageHigh
 * @param tooHotLow
 * @param tooHotHigh
 * @param doorOpenHumidityDerivativeThreshold
 * @param fridgeLink
 * @param fridgeName
 */
case class FridgeModule(
  fridgeHumidity: Entity[Double],
  fridgeTemperature: Entity[Double],
  averageCoolingPower: Expr[Double],
  averageStandbyPower: Expr[Double],
  entityNamePrefix: String = "fridge",
  humidityThreshold: Expr[Double] = Const(60.0),
  tooHumidRatioPercentageLow: Expr[Double] = Const(80),
  tooHumidRatioPercentageHigh: Expr[Double] = Const(90),
  tooHotLow: Expr[Double] = Const(7),
  tooHotHigh: Expr[Double] = Const(8),
  doorOpenHumidityDerivativeThreshold: Expr[Double] = Const(2),
  fridgeLink: Option[String] = None,
  fridgeName: String = "Fridge",
) extends SimpleHagenModule:

  private val humidityOkDef = BinarySensorDef(
    name = s"$entityNamePrefix humidity ok",
    state = fridgeHumidity < humidityThreshold
  )

  private val humidityOkRatioDef = RawTemplate[Double](
    domain="sensor",
    name=s"$entityNamePrefix humidity ok last 24h ratio",
    rawDef = Map(
      "platform" -> "history_stats",
      "entity_id" -> humidityOkDef.entity.name,
      "state" -> "on",
      "type" -> "ratio",
      "end" -> "{{ now() }}",
      "duration" -> Map("hours" -> 24)
    ),
    variables = Set(humidityOkDef.entity),
  )

  private val tooHumidSensorDef = hysteresisSensor(
    s"${entityNamePrefix}_too_humid_24h_hysteresis",
    humidityOkRatioDef.entity,
    low = tooHumidRatioPercentageLow,
    high = tooHumidRatioPercentageHigh,
    aboveHigh = false,
  )

  private val tooHotSensorDef = hysteresisSensor(
    s"${entityNamePrefix}_too_hot_hysteresis",
    fridgeTemperature,
    low = tooHotLow,
    high = tooHotHigh,
    aboveHigh = true,
  )

  private val tooHumidBooleanDef = InputBooleanDef(s"$entityNamePrefix too humid 24h")

  private val tooHotBooleanDef = InputBooleanDef(s"$entityNamePrefix too hot")

  private val outOfRangeAutomations = hysteresisAutomation(
    s"$entityNamePrefix too humid automation",
    s"$entityNamePrefix too humid automation",
    additionalTriggers = Seq(),
    additionalConditions = Seq(),
    switchable = tooHumidBooleanDef.entity,
    hysteresisEntity = tooHumidSensorDef.entity,
  ) ++ hysteresisAutomation(
    s"$entityNamePrefix too hot automation",
    s"$entityNamePrefix too hot automation",
    additionalTriggers = Seq(),
    additionalConditions = Seq(),
    switchable = tooHotBooleanDef.entity,
    hysteresisEntity = tooHotSensorDef.entity,
  )

  private val humidityDerivativeDef = DerivativeSensorDef(
    source = fridgeHumidity,
    unitTime = "min",
    name = s"$entityNamePrefix humidity derivative",
  )

  private val doorOpenDef = BinarySensorDef(
    name = s"$entityNamePrefix door open",
    state = humidityDerivativeDef.entity > doorOpenHumidityDerivativeThreshold
  )

  private val doorOpenCounter = CounterDef(s"$entityNamePrefix door open counter")

  private val doorOpenAutomation = Automation(
    id = s"${entityNamePrefix}_door_open_inc",
    alias = s"$entityNamePrefix door open",
    triggers = Seq(Change(doorOpenDef.entity).to(true)),
    conditions = Seq(),
    actions = Seq(
      doorOpenCounter.entity.increment(),
    )
  )

  private val highlightables = Seq(
    Highlightable(
      id = s"${entityNamePrefix}_too_humid_24h",
      conditions = Seq(
        SimplePositiveCondition(
          entity = tooHumidBooleanDef.entity,
          value = true
        )
      ),
      icon = "mdi:fridge",
      priority = 24,
      link = fridgeLink,
      description = Const(s"$fridgeName has been too humid last 24h"),
      notification = true,
    ),
    Highlightable(
      id = "${entityNamePrefix}_too_hot",
      conditions = Seq(
        SimplePositiveCondition(tooHotBooleanDef.entity, true)
      ),
      icon = "mdi:fridge",
      priority = 2,
      link = fridgeLink,
      description = Const(s"$fridgeName is too hot"),
      notification = true,
    ),
  )

  private val temperatureDerivativeDef = DerivativeSensorDef(
    name = s"${entityNamePrefix} temperature derivative",
    source = fridgeTemperature,
    unitTime = "min",
  )

  // Estimate power based on temperature change. When temperature goes down, we assume it is cooling. When it goes up,
  // we assume it is in standby. We ignore the fridge light when the door is open, as it is negligible.
  // This estimate is delayed, but both start of cooling and end of cooling are delayed. If they are delayed by the same
  // time, it shouldn't affect the accuracy of the energy sensor in long run.
  private val estimatedPowerDef = SensorDef(
    name = s"${entityNamePrefix}_estimated_power",
    unit = Some("W"),
    stateClass = Some("measurement"),
    state = (temperatureDerivativeDef.entity < Const(0.0)).matches(
      ifTrue = averageCoolingPower,
      ifFalse = averageStandbyPower
    ),
  )

  // TODO: addability to energy dashboard?
  private val estimatedEnergyDef = IntegrationSensorDef(
    name = s"$fridgeName energy",
    id = s"${entityNamePrefix}_energy",
    unitPrefix = "k",
    source = estimatedPowerDef.entity,
  )

  def content: HeteroMap[HagenKey[_]] = HeteroMap(
    Automations -> (outOfRangeAutomations ++ Seq(doorOpenAutomation)),
    InputBooleans -> Seq(tooHumidBooleanDef, tooHotBooleanDef),
    Templates -> Seq(tooHumidSensorDef, tooHotSensorDef, humidityOkDef, estimatedPowerDef, doorOpenDef),
    Highlightables -> highlightables,
    Sensors -> Seq(temperatureDerivativeDef, estimatedEnergyDef, humidityDerivativeDef, humidityOkRatioDef),
    Counters -> Seq(doorOpenCounter),
    SingleObject[String, FridgeEntities](fridgeName) -> FridgeEntities(
      humidity = fridgeHumidity,
      temperature = fridgeTemperature,
      humidityOk = humidityOkDef.entity,
      humidityOkRatio = humidityOkRatioDef.entity,
      doorOpen = doorOpenDef.entity,
      doorOpenCounter = doorOpenCounter.entity,
      estimatedPower = estimatedPowerDef.entity,
      estimatedEnergy = estimatedEnergyDef.entity,
    ),
  )

  // TODO: utility meter

