package com.v6ak.hagen.conditions

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.Entity

trait Condition extends Element:
  override def defined: Set[Entity[_]] = Set()
