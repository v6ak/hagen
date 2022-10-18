package com.v6ak.hagen.addons.breakingChanges

import com.v6ak.hagen.expressions.{Context, Entity, StringType}
import com.v6ak.hagen.output.{HagenKey, HagenModule, TopLevelKeys}
import com.v6ak.hagen.{Element, RawElement, TopLevelElement, haName, optionalMap}
import com.v6ak.{HeteroMap, hagen}

final case class BreakingChangesConfig(name: Option[String] = None) extends TopLevelElement:

  def toStructure(context: Context): Map[String, Element] = Map(
    "breaking_changes" -> RawElement(
      optionalMap("name", name),
      variables = Set(),
      defined = defined,
    )
  )

  override def variables: Set[Entity[_]] = Set()

  override def defined: Set[Entity[_]] = Set(
    Entity[String](haName("sensor", name.getOrElse("Potential breaking changes"))),
  )


