package com.v6ak.hagen.actions.unsafe

import com.v6ak.hagen.actions.Action
import com.v6ak.hagen.expressions.{Context, Entity}
import com.v6ak.hagen.materializeElements

final case class RawAction(structure: Map[String, _], variables: Set[Entity[_]]) extends Action {
  override def toStructure(context: Context): Any = materializeElements(context, structure)
}
