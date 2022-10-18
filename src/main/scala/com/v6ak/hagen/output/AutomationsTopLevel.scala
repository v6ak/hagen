package com.v6ak.hagen.output

import com.v6ak.HeteroMap
import com.v6ak.hagen.NakedElement
import com.v6ak.hagen.automation.Automation

class AutomationsTopLevel(@deprecated adjustAutomation: Automation => Automation = identity) extends HagenModule {

  override def dependencies: Set[HagenKey[_]] = Set(Automations)

  override def produces: Set[HagenKey[_]] = Set(TopLevelKeys)

  override def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] = {
    HeteroMap(
      TopLevelKeys -> Map(
        params(Automations).map(adjustAutomation).map(auto => s"automation ${auto.id}" -> NakedElement(auto)) *
      )
    )
  }

}
