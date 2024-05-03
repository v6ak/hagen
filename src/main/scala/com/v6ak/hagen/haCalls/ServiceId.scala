package com.v6ak.hagen.haCalls

object ServiceId:
  val ReloadAll: ServiceId = ServiceId("homeassistant", "reload_all")
  val Restart: ServiceId = ServiceId("homeassistant", "restart")

  def reload(domain: String) = ServiceId(domain, "reload")

case class ServiceId(domain: String, name: String):
  override def toString: String = s"$domain.$name"
