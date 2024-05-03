package com.v6ak.hagen.expressions

import com.v6ak.hagen
import com.v6ak.hagen.actions.ServiceCall

import scala.concurrent.duration.Duration

final case class Timer(name: String) extends Entity[TimerState]:
  def start(duration: Option[Expr[Duration]]) =
    ServiceCall("timer.start", this, hagen.optionalMap("duration", duration))

  def change(duration: Option[Expr[Duration]]) =
    ServiceCall("timer.change", this, Map("duration" -> duration))

  def cancel() = ServiceCall("timer.cancel", this)

  def finish() = ServiceCall("timer.finish", this)

  def pause() = ServiceCall("timer.pause", this)


  def durationStr: StateAttr[String] = StateAttr(this, "duration")

  def duration: Expr[Duration] = asTimeDelta(durationStr)

  def finishesAtStr: StateAttr[String] = StateAttr(this, "finishes_at")

  def finishesAt: Expr[Instant] = finishesAtStr.asDatetime

  def remainingStr: StateAttr[String] = StateAttr(this, "remaining")

  def remaining: Expr[Duration] = asTimeDelta(remainingStr)

  def actuallyRemaining: Expr[Duration] = finishesAt - now()

  def restore: StateAttr[Boolean] = StateAttr(this, "restore")

  def remainingRatio: Expr[Double] = actuallyRemaining / duration

  def remainingPercentage: Expr[Double] = remainingRatio * Const(100)

  def doneRatio: Expr[Double] = Const(1) - remainingRatio

  def donePercentage: Expr[Double] = doneRatio * Const(100)
