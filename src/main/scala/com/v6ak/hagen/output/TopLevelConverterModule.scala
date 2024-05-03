package com.v6ak.hagen.output

import com.v6ak.HeteroMap
import com.v6ak.hagen.Element
import com.v6ak.hagen.haCalls.ServiceId

abstract class TopLevelConverterModule[T](source: HagenKey[T], reloadService: ServiceId) extends HagenModule:

  def domain: String

  def convert(value: T): Map[String, Element]

  override def dependencies: Set[HagenKey[_]] = Set(source)

  override def produces: Set[HagenKey[_]] = Set(TopLevelKeys)

  override def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] =
    HeteroMap(
      TopLevelKeys -> convert(params(source)),
      // TODO: reload-related information
    )
