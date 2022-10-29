package com.v6ak.hagen.extensions.highlights

import com.v6ak.HeteroMap
import com.v6ak.hagen.actions.Notification
import com.v6ak.hagen.automation
import com.v6ak.hagen.automation.Automation
import com.v6ak.hagen.conditions.TemplateCondition
import com.v6ak.hagen.expressions.{And, Const, StringType}
import com.v6ak.hagen.expressions.BooleanOps.unary_!
import com.v6ak.hagen.output.{Automations, HagenKey, HagenModule}

case class HighlightableNotifications(autoClear: Boolean) extends HagenModule:

  override def dependencies: Set[HagenKey[_]] = Set(Highlightables)

  override def produces: Set[HagenKey[_]] = Set(Automations)

  override def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] = {
    val highlightables: Seq[Highlightable] = params(Highlightables)
    HeteroMap(
      Automations -> highlightables.filter(_.notification).flatMap { highlightable =>
        val idBase = s"highlight_notification_${highlightable.id}"
        Seq(
          Some(Automation(
            id = idBase,
            alias = s"$idBase",
            triggers = highlightable.conditions.map(_.toImperfectTrigger),
            conditions = Seq(TemplateCondition(And.of(highlightable.conditions.map(_.toExpr)))),
            actions = Seq(
              Notification(
                message = highlightable.description,
                group = highlightable.group,
                url = highlightable.link,
                clickAction = highlightable.link,
                icon = Some(highlightable.icon.toStructure),
                tag = Some(idBase),
              ),
            )
          )),
          if autoClear then Some(Automation(
            id = s"${idBase}_clear",
            alias = s"clear $idBase",
            triggers = highlightable.conditions.map(c => (!c).toImperfectTrigger),
            conditions = Seq(TemplateCondition(!And.of(highlightable.conditions.map(_.toExpr)))),
            actions = Seq(
              Notification(
                message = Const("clear_notification"),
                tag = Some(idBase),
              )
            ),
          )) else None
        ).flatten
      }
    )
  }
