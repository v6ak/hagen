package com.v6ak

import com.v6ak.hagen.expressions.Context

import scala.concurrent.duration.Duration

package object hagen:

  def haName(entityType: String, name: String): String =
    entityType + "." + name.replaceAll("[ :_]+", "_").toLowerCase()

  def ifNonDefault[T](value: T, defaultValue: T): Option[T] = if value == defaultValue then None else Some(value)

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
    Map("seconds" -> duration.toSeconds)
  }
