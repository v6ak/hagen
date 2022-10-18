package com.v6ak.hagen.extensions.hysteresis

enum HysteresisStatus(name: String):
  case On extends HysteresisStatus("on")
  case Off extends HysteresisStatus("off")
  case Keep extends HysteresisStatus("keep")
  override def toString: String = name

object HysteresisStatus:
  def from(status: Boolean): HysteresisStatus = status match
    case true => HysteresisStatus.On
    case false => HysteresisStatus.Off
