package com.v6ak.hagen.dashboards

import com.v6ak.hagen.dashboards.Button.DefaultShowIcon
import com.v6ak.hagen.expressions.{Context, Entity}
import com.v6ak.hagen.{Element, ifNonDefault, optionalMap}

// https://www.home-assistant.io/dashboards/entities/
case class ButtonEntity[T](
  name: Option[String] = None,
  icon: Option[Icon] = None,
  tapAction: Option[ButtonAction] = None,
  actionName: Option[String] = None,
  entity: Option[Entity[_]] = None,
) extends Element:
  override def toStructure(context: Context): Any = (
    Map(
      "type" -> "button",
    ) ++ (
      optionalMap("name", name) ++
      optionalMap("icon", icon.map(_.toStructure)) ++
      optionalMap("tap_action", tapAction.map(_.toStructure(context))) ++
      optionalMap("entity", entity.map(_.name)) ++
      optionalMap("action_name", actionName)
    )
  )

  override def defined: Set[Entity[_]] = Set()

  override def variables: Set[Entity[_]] = tapAction.fold(Set())(_.variables) ++ entity
