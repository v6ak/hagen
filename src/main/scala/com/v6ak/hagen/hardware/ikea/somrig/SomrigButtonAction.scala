package com.v6ak.hagen.hardware.ikea.somrig

enum SomrigButtonAction(val name: String):
  override def toString: String = name

  case InitialPress extends SomrigButtonAction("initial_press")
  case LongPress extends SomrigButtonAction("long_press")
  case ShortRelease extends SomrigButtonAction("short_release")
  case LongRelease extends SomrigButtonAction("long_release")
  case DoublePress extends SomrigButtonAction("double_press")
