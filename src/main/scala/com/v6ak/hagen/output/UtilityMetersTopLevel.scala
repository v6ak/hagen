package com.v6ak.hagen.output

import com.v6ak.HeteroMap

object UtilityMetersTopLevel extends HagenModule:

  override def dependencies: Set[HagenKey[_]] = Set(UtilityMeters)

  override def produces: Set[HagenKey[_]] = Set(TopLevelKeys)

  override def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] = HeteroMap(
    TopLevelKeys -> Map(
      "utility_meter" -> MapTuplesElement(params(UtilityMeters))
    )
  )
