package com.v6ak.hagen.modules

import com.v6ak.HeteroMap
import com.v6ak.hagen.expressions.Context
import com.v6ak.hagen.{Element, TopLevelElement}
import com.v6ak.hagen.output.{HagenKey, HagenModule, TopLevelKeys}

final case class ConfigModule(element: TopLevelElement) extends HagenModule:

  override def dependencies: Set[HagenKey[_]] = Set()

  override def produces: Set[HagenKey[_]] = Set(TopLevelKeys)

  override def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] = HeteroMap(
    TopLevelKeys -> element.toStructure(Context.TemporaryHack)
  )
