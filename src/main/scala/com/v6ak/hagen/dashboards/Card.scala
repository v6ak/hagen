package com.v6ak.hagen.dashboards

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.Entity

trait Card extends Element:
  override def defined: Set[Entity[_]] = Set()
