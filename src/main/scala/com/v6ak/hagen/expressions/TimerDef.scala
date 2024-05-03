package com.v6ak.hagen.expressions

import com.v6ak.hagen.output.{HagenKey, Timers}
import com.v6ak.hagen.*

import scala.concurrent.duration.FiniteDuration

final case class TimerDef(
  name: String,
  duration: Option[FiniteDuration] = None,
  friendlyName: Option[String] = None,
  icon: Option[String] = None,
  restore: Boolean = false,
) extends TupleElement with EntityDef[Timer, Seq[TimerDef]]:

  override def key: HagenKey[Seq[TimerDef]] = Timers

  override def createHagenDefinition: Seq[TimerDef] = Seq(this)

  def entity = Timer(haName("timer", name))

  override def toStructure(context: Context): (String, Map[_, _]) = entity.baseName -> toInnerStructure

  def toInnerStructure =
    Map(
      "name" -> friendlyName.getOrElse(name),
    ) ++
      optionalMap("duration", duration) ++
      optionalMap("icon", icon) ++
      mapIfNonDefault("restore", restore, false)

  override def variables: Set[Entity[_]] = Set()

  override def defined: Set[Entity[_]] = Set(entity)
