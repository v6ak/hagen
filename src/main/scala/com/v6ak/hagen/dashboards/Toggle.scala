package com.v6ak.hagen.dashboards
import com.v6ak.hagen.expressions.{Context, Entity}

object Toggle extends ButtonAction:
  override def toStructure(context: Context): Any = Map("action"->"toggle")
  override def variables: Set[Entity[_]] = Set()
