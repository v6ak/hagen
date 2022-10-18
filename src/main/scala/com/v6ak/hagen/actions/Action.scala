package com.v6ak.hagen.actions

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.Entity

trait Action extends Element:
  override def defined: Set[Entity[_]] = Set()
