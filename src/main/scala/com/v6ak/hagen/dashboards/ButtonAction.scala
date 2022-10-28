package com.v6ak.hagen.dashboards

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.Entity

trait ButtonAction extends Element:
  override def defined: Set[Entity[_]] = Set()
  //def confirmation: Option[Confirmation]
  protected def confirmationMap: Map[String, Any] = Map() // optionalMap("confirmation", confirmation.map(_.toStructure(Context.TH))
