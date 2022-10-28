package com.v6ak.hagen.dashboards

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.{Context, Entity}

trait Icon extends Element{
  override final def toStructure(context: Context): Any = toStructure
  override def variables: Set[Entity[_]] = Set()
  override def defined: Set[Entity[_]] = Set()
  def toStructure: String
}

object Icon {
  def apply(name: String): Icon = RawIcon(name)
  // TODO: object Mdi ha:port/static/mdi/iconList.json
}

private case class RawIcon(name: String) extends Icon:
  override def toStructure: String = name
