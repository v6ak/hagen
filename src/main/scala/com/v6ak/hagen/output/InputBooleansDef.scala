package com.v6ak.hagen.output

import com.v6ak.hagen.expressions.{Context, InputBooleanDef}
import com.v6ak.hagen.{DelegatingElement, Element}

case class InputBooleansDef(seq: Seq[InputBooleanDef]) extends DelegatingElement:
  override def toStructure(context: Context): Any = Map("input_boolean" -> Map(seq.map(_.toStructure(context)) *))

  override def subElements: Set[Element] = seq.toSet
