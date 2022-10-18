package com.v6ak.hagen.output

import com.v6ak.HeteroMap

object InputBooleansTopLevel extends HagenModule {

  override def dependencies: Set[HagenKey[_]] = Set(InputBooleans)

  override def produces: Set[HagenKey[_]] = Set(TopLevelKeys)

  override def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] = {
    HeteroMap(
      TopLevelKeys -> Map(
        "input_boolean" -> MapTuplesElement(params(InputBooleans))
      )
    )
  }

}
