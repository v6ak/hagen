package com.v6ak.hagen.dashboards
import com.v6ak.hagen.dashboards.Button.*
import com.v6ak.hagen.{ifNonDefault, optionalMap}
import com.v6ak.hagen.expressions.{Context, Entity}

// https://www.home-assistant.io/dashboards/button/
case class Button(
  name: Option[String] = None,
  icon: Option[Icon] = None,
  iconHeight: Option[String] = None,
  tapAction: Option[ButtonAction] = None,
  entity: Option[Entity[_]] = None,
  showIcon: Boolean = DefaultShowIcon,
  showName: Boolean = DefaultShowName,
) extends Card:
  override def toStructure(context: Context): Any = (
    Map(
      "type" -> "button",
    ) ++
      optionalMap("name", name) ++
      optionalMap("icon", icon.map(_.toStructure)) ++
      optionalMap("icon_height", iconHeight) ++
      optionalMap("tap_action", tapAction.map(_.toStructure(context))) ++
      optionalMap("show_icon", ifNonDefault(showIcon, DefaultShowIcon)) ++
      optionalMap("show_name", ifNonDefault(showName, DefaultShowName)) ++
      optionalMap("entity", entity.map(_.name))
  )
  override def variables: Set[Entity[_]] = tapAction.fold(Set())(_.variables) ++ entity

object Button:
  val DefaultShowIcon = true
  val DefaultShowName = true
