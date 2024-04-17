package com.v6ak.hagen.hardware.ikea.rodret

enum RodretAction(val name: String):
  case On extends RodretAction("on")
  case Off extends RodretAction("off")
  case BrightnessMoveDown extends RodretAction("brightness_move_down")
  case BrightnessMoveUp extends RodretAction("brightness_move_up")
  case BrightnessStop extends RodretAction("brightness_stop")

  override def toString: String = name
