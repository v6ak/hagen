package com.v6ak.hagen.automation

import com.v6ak.hagen.expressions.{Context, Entity}

case class RawTrigger(structure: Any, override val variables: Set[Entity[_]]) extends Trigger:
  override def toStructure(context: Context): Any = structure
