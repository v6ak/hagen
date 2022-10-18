package com.v6ak.hagen.extensions.highlights

import com.v6ak.hagen.expressions.Context
import com.v6ak.hagen.{DelegatingElement, Element}

class HighlightablesDef(highlightables: Seq[Highlightable]) extends DelegatingElement:
  override def subElements: Set[Element] = highlightables.toSet

  override def toStructure(context: Context): Any = Map("highlightables_hagen" -> highlightables.map(_.toStructure(context)))
