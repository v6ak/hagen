package com.v6ak.hagen

import com.v6ak.hagen.expressions.{Context, Entity, Expr, Instant, now}

import scala.annotation.targetName
import scala.concurrent.duration.Duration

object Period:
  @targetName("byFromTo") def apply(start: Expr[Instant], end: Expr[Instant]): FromTo = FromTo(start = start, end = end)

  @targetName("byFrom") def apply(start: Expr[Instant], duration: Duration): From = From(start = start, duration = duration)

  @targetName("byTo") def apply(end: Expr[Instant], duration: Duration): To = To(end = end, duration = duration)

  def untilNow(start: Expr[Instant]): FromTo = FromTo(start = start, end = now())

  def untilNow(duration: Duration): To = To(duration = duration, end = now())

  final case class FromTo(start: Expr[Instant], end: Expr[Instant]) extends Period:
    override def startOption: Some[Expr[Instant]] = Some(start)

    override def endOption: Some[Expr[Instant]] = Some(end)

    override def durationOption: None.type = None

  final case class From(start: Expr[Instant], duration: Duration) extends Period:
    override def startOption: Some[Expr[Instant]] = Some(start)

    override def endOption: None.type = None

    override def durationOption: Some[Duration] = Some(duration)

  final case class To(end: Expr[Instant], duration: Duration) extends Period:
    override def startOption: None.type = None

    override def endOption: Some[Expr[Instant]] = Some(end)

    override def durationOption: Some[Duration] = Some(duration)

abstract sealed class Period extends Element:
  def startOption: Option[Expr[Instant]]

  def endOption: Option[Expr[Instant]]

  def durationOption: Option[Duration]

  final override def toStructure(context: Context): Map[String, Any] =
    optionalMap("start", startOption.map(_.toStructure(context))) ++
      optionalMap("end", endOption.map(_.toStructure(context))) ++
      optionalMap("duration", durationOption.map(durationToMap))


  final override def variables: Set[Entity[_]] =
    startOption.fold(Set.empty)(_.variables) ++
      endOption.fold(Set.empty)(_.variables)

  final override def defined: Set[Entity[_]] = Set()