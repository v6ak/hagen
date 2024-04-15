package com.v6ak.hagen.automation

import com.v6ak.hagen.{durationToMap, optionalMap}
import com.v6ak.hagen.expressions.{Context, Entity, Expr}

import scala.concurrent.duration.Duration

final case class TemplateTrigger(
  condition: Expr[Boolean],
  duration: Option[Duration] = None,
) extends Trigger[Nothing]:
  override def toStructure(context: Context): Any = Map(
    "platform" -> "template",
    "value_template" -> condition.toStructure(context),
  )
    ++ optionalMap("for", duration.map(duration => durationToMap(duration)))

  def duration(duration: Duration): TemplateTrigger = copy(duration = Some(duration))

  override def variables: Set[Entity[_]] = condition.variables
