package com.v6ak.hagen.output

import com.v6ak.HeteroMap

object CountersTopLevel extends HagenModule{
  override def dependencies: Set[HagenKey[_]] = Set(Counters)

  override def produces: Set[HagenKey[_]] = Set(TopLevelKeys)

  override def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] = {
    HeteroMap(
      TopLevelKeys -> Map(
        "counter" -> MapTuplesElement(params(Counters))
      )
    )
  }

}
