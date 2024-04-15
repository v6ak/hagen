package com.v6ak.hagen.output
import com.v6ak.HeteroMap
import com.v6ak.hagen.actions.Action
import com.v6ak.hagen.expressions.{Context, Expr}

object WakeOnLanModule extends SimpleHagenModule:
  override def content: HeteroMap[HagenKey[_]] = HeteroMap(
    TopLevelKeys -> Map("wake_on_lan" -> MapElement(Map()))
  )
