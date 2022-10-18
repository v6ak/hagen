package com.v6ak.hagen.actions

import com.v6ak.hagen
import com.v6ak.hagen.expressions.{Const, Context, Entity, Expr}
import com.v6ak.hagen.optionalMap
import com.v6ak.hagen.expressions.StringType

final case class Notification(
  message: Expr[String],
  group: Option[String] = None,
  url: Option[String] = None,
  clickAction: Option[String] = None,
  icon: Option[String] = None,
  tag: Option[String] = None,
) extends Action:

  override def toStructure(context: Context): Any = Map(
    "service" -> "notify.notify",
    "data" -> Map(
      "message" -> message.asCompleteJinja(context),
      "data" -> (
        optionalMap("group", group) ++
        optionalMap("url", url) ++
        optionalMap("clickAction", clickAction) ++
        optionalMap("notification_icon", icon) ++
        optionalMap("tag", tag)
      )
    )
  )

  override def variables: Set[Entity[_]] = Set()
