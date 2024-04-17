package com.v6ak.hagen.hardware.ikea.somrig

enum SomrigButton(val name: String):
  override def toString: String = name

  case One extends SomrigButton("1")
  case Two extends SomrigButton("2")
