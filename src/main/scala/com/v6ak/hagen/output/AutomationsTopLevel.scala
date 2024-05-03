package com.v6ak.hagen.output

import com.v6ak.hagen.automation.Automation
import com.v6ak.hagen.haCalls.ServiceId
import com.v6ak.hagen.{Element, NakedElement}

class AutomationsTopLevel(@deprecated adjustAutomation: Automation => Automation = identity)
  extends TopLevelConverterModule(Automations, ServiceId.reload("automation")):

  override def domain: String = "automation"

  override def convert(automations: Seq[Automation]): Map[String, Element] = Map(
    automations.map(adjustAutomation).map(auto => s"automation ${auto.id}" -> NakedElement(auto)) *
  )
