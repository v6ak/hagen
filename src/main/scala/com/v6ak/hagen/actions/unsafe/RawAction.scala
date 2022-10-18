package com.v6ak.hagen.actions.unsafe

import com.v6ak.hagen.actions.Action
import com.v6ak.hagen.expressions.{Context, Entity}

final case class RawAction(structure: Any, variables: Set[Entity[_]]) extends Action {
  override def toStructure(context: Context): Any = structure
}
