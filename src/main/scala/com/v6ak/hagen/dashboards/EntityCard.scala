package com.v6ak.hagen.dashboards

import com.v6ak.hagen.optionalMap
import com.v6ak.hagen.expressions.{Context, ContextDependentEntity, Entity, StateAttr}

case class EntityCard(
  entity: ContextDependentEntity[_],
  icon: Option[Icon] = None,
  attribute: Option[String] = None,
  name: Option[String] = None,
  unit: Option[String] = None,
) extends Card:
  override def toStructure(context: Context): Any = (
    Map(
      "type" -> "entity",
      "entity" -> entity.getName(context),
    ) ++
      optionalMap("icon", icon.map(_.toStructure)) ++
      optionalMap("attribute", attribute) ++
      optionalMap("name", name) ++
      optionalMap("unit", unit)
  )

  override def variables: Set[Entity[_]] = entity.variables

object EntityCard:
  def forAttr(
    attr: StateAttr[_],
    icon: Option[Icon] = None,
    name: Option[String] = None,
  ): EntityCard = EntityCard(
    entity = attr.entity,
    attribute = Some(attr.name),
    icon = icon,
    name = name
  )