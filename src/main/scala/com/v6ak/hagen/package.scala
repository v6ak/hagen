package com.v6ak

package object hagen:

  def haName(entityType: String, name: String): String =
    entityType + "." + name.replaceAll("[ :_]+", "_").toLowerCase()

  def optionalMap[T](name: String, valueOption: Option[T]): Map[String, T] = valueOption.fold(Map.empty)(value =>
    Map(name -> value)
  )

  def mapIfNonEmpty[T](name: String, value: Seq[T]): Map[String, Seq[T]] = value match
    case Seq() => Map()
    case nonEmpty => Map(name -> nonEmpty)

