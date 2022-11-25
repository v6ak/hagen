package com.v6ak.hagen.extensions.highlights

import com.v6ak.hagen.Element
import com.v6ak.hagen.automation.Trigger
import com.v6ak.hagen.expressions.{Entity, Expr}

abstract class SimpleCondition[T] extends Element:

  def entity: Entity[T]

  def toExpr: Expr[Boolean]

  def toImperfectTrigger: Trigger[_]

  def unary_! : SimpleCondition[T]
