package com.v6ak.hagen.dashboards

import com.v6ak.HeteroMap
import com.v6ak.hagen.output.{HagenKey, HagenModule, SimpleHagenModule}

class DashboardCompositionModule(
  id: String,
  name: String,
  title: String,
  icon: Icon,
  showInSidebar: Boolean,
)(pageNames: String*) extends HagenModule:
  override def dependencies: Set[HagenKey[_]] = Set(DashboardPages)

  override def produces: Set[HagenKey[_]] = Set(Dashboards)

  override def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] = {
    val pages = params(DashboardPages)
    HeteroMap(
      Dashboards -> Map(
        id -> Dashboard(
          name = name, title = title, icon = icon, showInSidebar = showInSidebar
        )(pageNames.map(name => pages(name)) *)
      )
    )
  }
