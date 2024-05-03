package com.v6ak.hagen.expressions

import com.v6ak.hagen.*
import com.v6ak.hagen.output.{HagenKey, InputNumbers}

final case class InputNumberDef[T <: Int | Double](
  name: String,
  min: T,
  max: T,
  step: T = 1,
  mode: InputNumberDef.Mode = InputNumberDef.Mode.Slider,
  friendlyName: Option[String] = None,
  initial: Option[T] = None,
  icon: Option[String] = None,
  unit: Option[String] = None,
)(implicit jinjaType: Type[T]) extends TupleElement with EntityDef[InputNumber[T], Seq[InputNumberDef[?]]]:
  override def key: HagenKey[Seq[InputNumberDef[?]]] = InputNumbers

  override def createHagenDefinition: Seq[InputNumberDef[T]] = Seq(this)

  def entity = InputNumber(haName("input_number", name))

  override def toStructure(context: Context): (String, Map[_, _]) = entity.baseName -> toInnerStructure

  def toInnerStructure =
    Map(
      "min" -> min,
      "max" -> max,
      "name" -> friendlyName.getOrElse(name),
    ) ++
      mapIfNonDefault("step", step, 1) ++
      mapIfNonDefault("mode", mode, InputNumberDef.Mode.Slider).view.mapValues(_.name) ++
      optionalMap("name", friendlyName) ++
      optionalMap("initial", initial.map(jinjaType.serialize)) ++
      optionalMap("icon", icon) ++
      optionalMap("unit_of_measurement", unit)

  override def variables: Set[Entity[_]] = Set()

  override def defined: Set[Entity[_]] = Set(entity)


object InputNumberDef:
  enum Mode(val name: String):
    case Slider extends Mode("slider")
    case Box extends Mode("box")
