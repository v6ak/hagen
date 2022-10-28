package com.v6ak.hagen.dashboards
import com.v6ak.hagen.materializeElements
import com.v6ak.hagen.expressions.{Context, Entity}

case class RawButtonAction(structure: Map[String, Any], override val variables: Set[Entity[_]]) extends ButtonAction:
  override def toStructure(context: Context): Any = materializeElements(context, structure)
