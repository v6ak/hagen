package com.v6ak.hagen.conditions.unsafe

import com.v6ak.hagen.conditions.Condition
import com.v6ak.hagen.expressions.{Context, Entity}

final case class RawCondition(structure: Any, variables: Set[Entity[_]]) extends Condition:
  override def toStructure(context: Context): Any = structure

