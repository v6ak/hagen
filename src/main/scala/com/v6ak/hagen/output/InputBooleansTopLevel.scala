package com.v6ak.hagen.output

import com.v6ak.HeteroMap

object InputBooleansTopLevel extends HagenModule:

  override def dependencies: Set[HagenKey[?]] = Set(InputBooleans)

  override def produces: Set[HagenKey[?]] = Set(TopLevelKeys)

  override def content(params: HeteroMap[HagenKey[?]]): HeteroMap[HagenKey[?]] = {
    HeteroMap(
      TopLevelKeys -> Map(
        "input_boolean" -> MapTuplesElement(params(InputBooleans))
      )
    )
  }
