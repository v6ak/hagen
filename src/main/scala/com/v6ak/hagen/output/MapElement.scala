package com.v6ak.hagen.output

import com.v6ak.hagen.expressions.Context
import com.v6ak.hagen.{DelegatingElement, Element, TupleElement}

final case class MapElement(map: Map[String, Element]) extends DelegatingElement:
  override def toStructure(context: Context): Any = map.view.mapValues(_.toStructure(context)).toMap

  override def subElements: Set[Element] = map.values.toSet

object MapElement:
  def apply(tuples: (String, Element)*) = new MapElement(Map(tuples*))

final case class StringElement(s: String) extends DelegatingElement:
  override def toStructure(context: Context): Any = s

  override def subElements: Set[Element] = Set()

final case class MapTuplesElement(seq: Seq[TupleElement]) extends DelegatingElement:
  override def toStructure(context: Context): Map[String, Any] = seq.map(_.toStructure(context)).toMap

  override def subElements: Set[Element] = seq.toSet
