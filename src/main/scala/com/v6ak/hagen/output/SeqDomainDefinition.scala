package com.v6ak.hagen.output

import com.v6ak.hagen.Element
import com.v6ak.hagen.haCalls.ServiceId

abstract class SeqDomainDefinition[T <: Seq[Element]](source: HagenKey[T], val domain: String, reloadService: ServiceId)
  extends TopLevelConverterModule(source, reloadService):

  def this(source: HagenKey[T], domain: String) = this(source, domain, ServiceId.reload(domain))

  override def convert(value: T): Map[String, Element] = Map(
    domain -> SeqElement(value)
  )
