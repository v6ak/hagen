package com.v6ak.hagen.extensions.highlights

import com.v6ak.HeteroMap
import com.v6ak.hagen.{BinarySensorDef, Element}
import com.v6ak.hagen.dashboards.{Card, ConditionalCard, DashboardParts, HorizontalStack, Markdown, RawCard, Navigate}
import com.v6ak.hagen.expressions.{And, Const, Context, Entity, Expr, Or, StringType}
import com.v6ak.hagen.output.{HagenKey, HagenModule, Templates}

object HighlightableDashboard extends HagenModule:

  override def produces: Set[HagenKey[_]] = Set(DashboardParts, Templates)

  private def shownConditions(highlightables: Seq[Highlightable]) = {
    import com.v6ak.hagen.expressions.BooleanOps.unary_!
    import com.v6ak.hagen.expressions.BooleanType
    val (conditions, binarySensorDefs) = highlightables.map{h =>
      h.conditions match
        case Seq(condition) => (condition, Seq())
        case complexCondition =>
          val cond = BinarySensorDef(
            name = s"highlightable ${h.id} shown",
            state = And.of(complexCondition.map(_.toExpr))
          )
          (SimplePositiveCondition(cond.entity, true), Seq(cond))
    }.unzip
    (conditions, binarySensorDefs.flatten)
  }

  override def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] = {
    val highlightables: Seq[Highlightable] = params(Highlightables)
    val (allConditions, binarySensorDefs) = shownConditions(highlightables)
    HeteroMap(
      Templates -> binarySensorDefs,
      DashboardParts -> Map(
        "theme" -> "Backend-selected",
        "title" -> "Overview",
        "path" -> "overview",
        "type" -> "sidebar",
        "icon" -> "mdi:list-status",
        "badges" -> Seq(),
        "cards" -> (
          highlightables.sortBy(_.priority).map(highlightable =>
            ConditionalCard(
              conditions = highlightable.conditions,
              card = HorizontalStack(
                Seq(
                  Some(Markdown(
                    createDescription(highlightable),
                  )),
                  highlightable.link.map(link => RawCard(
                    Map(
                      "type" -> "button",
                      "name" -> ">>>>",
                      "icon" -> highlightable.icon, //.getOrElse("mdi:chevron-right"),
                      "tap_action" -> Navigate(link).toStructure(Context.TemporaryHack)
                    ),
                    variables = Set()
                  ))
                ).flatten*
              )
            )
          ) ++ Seq(
            ConditionalCard(
              allConditions.map(!_),
              Markdown("The most interesting fact about this place is that there is currently nothing interesting.")
            )
          )
        ).map(_.toStructure(Context.TemporaryHack))
      )
    )
  }

  private def createDescription(highlightable: Highlightable) = {
    /*val main = <ha-alert>{highlightable.description}</ha-alert>
    highlightable.link match
      case None => main
      case Some(link) => <a href={link} style="color: auto; text-decoration: none;" class="clickable-alert">{main}</a>*/
    import com.v6ak.hagen.expressions.StringOps._
    val main = Const("<ha-alert>") + highlightable.description + Const("</ha-alert>")
    highlightable.link match
      case None => main
      case Some(link) =>
        Const(s"""<a href="$link" style="color: auto; text-decoration: none;" class="clickable-alert">""")
          + main + Const("</a>")

  }

  override def dependencies: Set[HagenKey[_]] = Set(Highlightables)
