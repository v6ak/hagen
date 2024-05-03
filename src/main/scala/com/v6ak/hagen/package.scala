package com.v6ak

import com.v6ak.hagen.expressions.Context

import scala.concurrent.duration.Duration

package object hagen:

  def haName(entityType: String, name: String): String =
    def replaceLowerIndex(c: Char): Char = if c >= '₀' && c <= '₉' then (c - '₀' + '0').toChar else c
    entityType + "." + name.map(replaceLowerIndex).replaceAll("[ .:()_-]+", "_").replaceAll("(^_)|(_$)", "").toLowerCase()

  def ifNonDefault[T](value: T, defaultValue: T): Option[T] = if value == defaultValue then None else Some(value)

  def mapIfNonDefault[T](name: String, value: T, defaultValue: T): Map[String, T] = optionalMap(name, ifNonDefault(value, defaultValue))

  def optionalMap[T](name: String, valueOption: Option[T]): Map[String, T] = valueOption.fold(Map.empty)(value =>
    Map(name -> value)
  )

  def mapIfNonEmpty[T](name: String, value: Seq[T]): Map[String, Seq[T]] = value match
    case Seq() => Map()
    case nonEmpty => Map(name -> nonEmpty)

  def materializeElements(context: Context, map: Map[_, _]): Map[_, _] = map.view.mapValues(mapDataValue(context, _)).toMap

  private def mapDataValue(context: Context, e: Any): Any = e match
    case elem: Element => elem.toStructure(context)
    case map: Map[_, _] => materializeElements(context, map)
    case seq: Seq[_] => seq.map(mapDataValue(context, _))
    case other => other

  def durationToMap(duration: Duration): Map[String, Any] = {
    val wholeSeconds = duration.toSeconds % 60
    val wholeNanos = duration.toNanos % 1e9
    val verboseRes = Map[String, Long|Double](
      "seconds" -> (if wholeNanos == 0 then wholeSeconds else wholeSeconds + wholeNanos/1e9),
      "minutes" -> duration.toMinutes % 60,
      "hours" -> duration.toHours % 24,
      "days" -> duration.toDays,
    )
    val briefRes = verboseRes.filter(_._2 != 0L)
    if briefRes.nonEmpty then briefRes else Map("seconds" -> 0)
  }
