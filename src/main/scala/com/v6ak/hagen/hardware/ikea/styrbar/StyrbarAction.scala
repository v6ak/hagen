package com.v6ak.hagen.hardware.ikea.styrbar

enum StyrbarAction(val name: String):
  /*
  on    On    BrightnessMoveUp    BrightnessStop
  off   Off   BrightnessMoveDown  BrightnessStop
  left  Left  ArrowLeftHold       ArrowLeftRelease
  right Right ArrowRightHold      ArrowRightRelease
  */
  case ArrowLeftClick extends StyrbarAction("arrow_left_click")
  case ArrowLeftHold extends StyrbarAction("arrow_left_hold")
  case ArrowLeftRelease extends StyrbarAction("arrow_left_release")

  case ArrowRightClick extends StyrbarAction("arrow_right_click")
  case ArrowRightHold extends StyrbarAction("arrow_right_hold")
  case ArrowRightRelease extends StyrbarAction("arrow_right_release")

  case On extends StyrbarAction("on")
  case BrightnessMoveUp extends StyrbarAction("brightness_move_up")

  case Off extends StyrbarAction("off")
  case BrightnessMoveDown extends StyrbarAction("brightness_move_down")

  case BrightnessStop extends StyrbarAction("brightness_stop")

  override def toString: String = name