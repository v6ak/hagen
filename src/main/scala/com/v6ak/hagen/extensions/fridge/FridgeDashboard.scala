package com.v6ak.hagen.extensions.fridge

import com.v6ak.HeteroMap
import com.v6ak.hagen.dashboards.*
import com.v6ak.hagen.expressions.IntOps.*
import com.v6ak.hagen.expressions.{Const, Context, DoubleType, Entity, IntType}
import com.v6ak.hagen.extensions.fridge.{FridgeEntities, FridgeModule}
import com.v6ak.hagen.output.{HagenKey, HagenModule, SingleObject}


class FridgeDashboard(
  absoluteHumiditySensor: Option[Entity[Double]] = None,
  dewPointSensor: Option[Entity[Double]] = None,
  id: String = "Fridge",
) extends HagenModule:

  override def produces: Set[HagenKey[_]] = Set(DashboardPages)

  override def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] = {
    val fridgeEntities: FridgeEntities = params(fridgeSensorsKey)
    val basicEntities = Seq(
      Some(StatsEntity(fridgeEntities.temperature, name = Some("temperature"))),
      Some(StatsEntity(fridgeEntities.humidity, name = Some("humidity"))),
      absoluteHumiditySensor.map(StatsEntity(_, name = Some("absolute humidity"))),
      dewPointSensor.map(StatsEntity(_, name = Some("dew point"))),
    ).flatten
    val fridgePage = DashboardPage(
      title = "Fridge",
      path = "fridge",
      pageType = DashboardPageType.Masonry,
      icon = Icon("mdi:fridge")
    )(
      Glance()(basicEntities ++ Seq(
        Some(StatsEntity(fridgeEntities.estimatedPower, name = Some("estimated power"))),
      ).flatten*),
      HistoryGraph(
        hoursToShow = 7 * 24,
      )(basicEntities ++ Seq(
        Some(StatsEntity(fridgeEntities.doorOpenDaily, name = Some("door open count daily"))),
      ).flatten*),
      HistoryGraph()(
        StatsEntity(fridgeEntities.doorOpen),
        StatsEntity(fridgeEntities.humidityOkRatio),
      ),
      Glance()(
        GlanceEntity(
          fridgeEntities.doorOpenDaily,
          icon = Some(Icon("mdi:door-open")),
          name = Some("door open count daily"),
        )
      ),
    )
    HeteroMap(
      DashboardPages -> Map("fridge" -> fridgePage)
    )
  }

  private val fridgeSensorsKey = SingleObject[String, FridgeEntities](id)

  override def dependencies: Set[HagenKey[_]] = Set(fridgeSensorsKey)

