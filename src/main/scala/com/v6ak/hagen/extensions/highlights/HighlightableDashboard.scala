package com.v6ak.hagen.extensions.highlights

import com.v6ak.HeteroMap
import com.v6ak.hagen.{BinarySensorDef, Element}
import com.v6ak.hagen.dashboards.{Button, Card, ConditionalCard, DashboardPage, DashboardPageType, DashboardPages, HorizontalStack, Icon, Markdown, MdiIcons, Navigate, VerticalStack}
import com.v6ak.hagen.expressions.*
import com.v6ak.hagen.output.{HagenKey, HagenModule, Templates}

case class HighlightableDashboard(footerCards: Seq[Card] = Seq()) extends HagenModule:

  override def produces: Set[HagenKey[_]] = Set(DashboardPages, Templates)

  private def shownConditions(highlightables: Seq[Highlightable]) = {
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
      DashboardPages -> Map(
        "overview" -> DashboardPage(
          title = "Overview",
          path = "overview",
          icon = MdiIcons.ListStatus,
          pageType = DashboardPageType.Masonry
        )(VerticalStack(
          highlightables.sortBy(_.priority).map(highlightable =>
            ConditionalCard(
              conditions = highlightable.conditions,
              card = HorizontalStack(
                Seq(
                  Some(Markdown(
                    createDescription(highlightable),
                  )),
                  Some(Button(
                    name = highlightable.link.map(_ => ">>>>"),
                    icon = Some(highlightable.icon),
                    tapAction = highlightable.link.map(Navigate.apply(_)),
                  )),
                ).flatten *
              )
            )
          ) ++ Seq(
            ConditionalCard(
              allConditions.map(!_),
              Markdown("The most interesting fact about this place is that there is currently nothing interesting.")
            )
          ) ++ footerCards
        *))
      ),
    )
  }

  private def createDescription(highlightable: Highlightable) = {
    /*val main = <ha-alert>{highlightable.description}</ha-alert>
    highlightable.link match
      case None => main
      case Some(link) => <a href={link} style="color: auto; text-decoration: none;" class="clickable-alert">{main}</a>*/
    val safe = com.v6ak.hagen.expressions.unsafe.FilterExpr[String, String](_: Expr[String], "safe")
    import com.v6ak.hagen.expressions.StringOps
    val main = safe(Const("<ha-alert>")) + highlightable.description + safe(Const("</ha-alert>"))
    highlightable.link match
      case None => main
      case Some(link) =>
        safe(Const(s"""<a href="$link" style="color: auto; text-decoration: none;" class="clickable-alert"><font color="white">"""))
          + main + safe(Const("</font></a>"))

  }

  override def dependencies: Set[HagenKey[_]] = Set(Highlightables)
