package com.v6ak.hagen.automation

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.{Entity, Expr}

trait Trigger[+T] extends Element:
  override def defined: Set[Entity[_]] = Set()
