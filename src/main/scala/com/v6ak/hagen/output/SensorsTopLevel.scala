package com.v6ak.hagen.output

import com.v6ak.HeteroMap

object SensorsTopLevel extends HagenModule:

  override def dependencies: Set[HagenKey[?]] = Set(Sensors)

  override def produces: Set[HagenKey[?]] = Set(TopLevelKeys)

  override def content(params: HeteroMap[HagenKey[?]]): HeteroMap[HagenKey[?]] =
    HeteroMap(
      TopLevelKeys -> Map(
        "sensor" -> SeqElement(params(Sensors))
      )
    )
