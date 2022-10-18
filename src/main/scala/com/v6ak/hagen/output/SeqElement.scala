package com.v6ak.hagen.output

import com.v6ak.hagen.{DelegatingElement, Element}
import com.v6ak.hagen.expressions.{Context, Entity}

final case class SeqElement(seq: Seq[Element]) extends DelegatingElement:
  override def toStructure(context: Context): Any = seq.map(_.toStructure(context))
  override def subElements: Set[Element] = seq.toSet
