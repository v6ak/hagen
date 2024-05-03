package com.v6ak.hagen.expressions

import com.v6ak.hagen.output.{HagenKey, InputBooleans}
import com.v6ak.hagen.{EntityDef, TupleElement, expressions, haName, optionalMap}

final case class InputBooleanDef(
  name: String,
  initial: Option[Boolean] = None,
  icon: Option[String] = None
) extends TupleElement with EntityDef[InputBoolean, Seq[InputBooleanDef]]:
  override def key: HagenKey[Seq[InputBooleanDef]] = InputBooleans

  override def createHagenDefinition: Seq[InputBooleanDef] = Seq(this)

  def entity = InputBoolean(haName("input_boolean", name))

  override def toStructure(context: Context): (String, Map[_, _]) = entity.baseName -> toInnerStructure
  def toInnerStructure =
    Map(
      "name" -> s"friendly $name #2",
    ) ++
    optionalMap("initial", initial.map(BooleanType.serialize)) ++
    optionalMap("icon", icon)

  override def variables: Set[Entity[_]] = Set()

  override def defined: Set[Entity[_]] = Set(entity)
