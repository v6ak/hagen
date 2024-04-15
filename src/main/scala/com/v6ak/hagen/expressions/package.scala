package com.v6ak.hagen

import com.v6ak.hagen.expressions.unsafe.{FilterExpr, FuncCall}

import scala.concurrent.duration.Duration

package object expressions {
  def max[T <: Int|Double](expressions: Expr[T]*): Expr[T] = unsafe.FuncCall("max", expressions: _*)
  def min[T <: Int|Double](expressions: Expr[T]*): Expr[T] = unsafe.FuncCall("min", expressions: _*)

  def normalizeAngleDeg(angle: Expr[Double]): Expr[Double] = angle % Const(360)

  def sin(angle: Expr[Double]): Expr[Double]  = unsafe.FuncCall("sin", angle)
  def cos(angle: Expr[Double]): Expr[Double]  = unsafe.FuncCall("cos", angle)
  def tag(angle: Expr[Double]): Expr[Double]  = unsafe.FuncCall("cos", angle)
  def sinDeg(angle: Expr[Double]): Expr[Double]  = sin(angle * Pi / Const(180))
  def cosDeg(angle: Expr[Double]): Expr[Double]  = cos(angle * Pi / Const(180))
  def tagDeg(angle: Expr[Double]): Expr[Double]  = tag(angle * Pi / Const(180))

  val E = unsafe.RawExpr[Double]("e", variables = Set())
  val Pi = unsafe.RawExpr[Double]("pi", variables = Set())

  def exp(exponent: Expr[Double]): Expr[Double] = E ^ exponent

  def bounded[T <: Int | Double](value: Expr[T], lower: Expr[T], upper: Expr[T]): Expr[T] =
    max(
      lower,
      min(
        upper,
        value
      )
    )

  def abs[T <: Int|Double](expression: Expr[T]): Expr[T] = unsafe.FilterExpr(expression, "abs")

  def now(): Expr[Instant] = FuncCall("now")

  def timestampCustom(
    ts: Expr[Int],
    formatString: Option[Expr[String]] = None,
    local: Expr[Boolean] = Const(true),
    default: Option[Expr[String]] = None
  ): Expr[String] = unsafe.FilterExpr(
    ts,
    unsafe.FuncCall(
      "timestamp_custom",
      namedParams = Seq(
        formatString.map("format_string" -> _),
        Some(local).filterNot(_==Const(true)).map("local" -> _),
        default.map("default" -> _)
      ).flatten.toMap,
      params = Seq(),
    )
  )

  def timeDelta(
    weeks: Expr[Double] | Expr[Int] = Const(0),
    days: Expr[Double] | Expr[Int] = Const(0),
    hours: Expr[Double] | Expr[Int] = Const(0),
    minutes: Expr[Double] | Expr[Int] = Const(0),
    seconds: Expr[Double] | Expr[Int] = Const(0),
    milliseconds: Expr[Double] | Expr[Int] = Const(0),
    microseconds: Expr[Double] | Expr[Int] = Const(0),
  ): Expr[Duration] = {
    val fullParams = Seq(
      "weeks" -> weeks,
      "days" -> days,
      "hours" -> hours,
      "minutes" -> minutes,
      "seconds" -> seconds,
      "milliseconds" -> milliseconds,
      "microseconds" -> microseconds,
    )
    // Not needed, but it produces a nicer code when filtered
    val nonZeroParams = fullParams.filter(_._2 != Const(0)).filter(_._2 != Const(0.0))
    unsafe.FuncCall(name = "timedelta", namedParams = nonZeroParams*)
  }

  def escapeHtml(expr: Expr[String]): Expr[String] = FilterExpr(expr, "e")

  def asTimeDelta(expr: Expr[String]): Expr[Duration] = FilterExpr(expr, "as_timedelta")

  def stringify(expr: Expr[_]): Expr[String] = FilterExpr(expr, "string")

}
