package com.v6ak.hagen.expressions

import com.v6ak.hagen
import com.v6ak.hagen.actions.{Action, DecreaseBrightness, IncreaseBrightness, ServiceCall}
import com.v6ak.hagen.optionalMap

final case class Light(name: String) extends Entity[Boolean] with SwitchableEntity {
  override def entityType: String = "light"

  def effect: StateAttr[String] = StateAttr(this, "effect")

  def turnOn(
    colorTemp: Option[Expr[Double]] = None,
    brightnessPct: Option[Expr[Double]] = None,
    transition: Option[Expr[Double]] = None,
    effect: Option[Expr[String]] = None
  ): Action = ServiceCall(
    s"$entityType.turn_on", this, data =
      optionalMap("color_temp", colorTemp) ++
        optionalMap("brightness_pct", brightnessPct) ++
        optionalMap("transition", transition) ++
        optionalMap("effect", effect)
  )

  def decreateBrightness() = DecreaseBrightness(this)

  def increateBrightness() = IncreaseBrightness(this)

  def brightnessRaw: Expr[Int] = StateAttr(this, "brightness")

  def brightnessRatio: Expr[Double] = brightnessRaw / Const(255.0)
  def brightnessPercent: Expr[Double] = brightnessRatio * Const(100.0)

  def changeBrightness(diff: Expr[Double], transition: Option[Expr[Double]]): Action = turnOn(
    brightnessPct = Some(
      bounded(
        lower = Const(0.0),
        upper = Const(100.0),
        value = brightnessPercent + diff
      )
    ),
    transition = transition,
  )

}
