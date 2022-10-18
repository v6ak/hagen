package com.v6ak.hagen.expressions

import com.v6ak.hagen.{TupleElement, haName}

final case class CounterDef(name: String) extends TupleElement:

  def entity = Counter(haName("counter", name))

  override def toStructure(context: Context): (String, Map[_, _]) = entity.baseName -> toInnerStructure

  def toInnerStructure = Map(
    "name" -> name,
  )

  override def variables: Set[Entity[_]] = Set()

  override def defined: Set[Entity[_]] = Set(entity)
