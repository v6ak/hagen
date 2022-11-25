package com.v6ak.hagen

import com.v6ak.hagen.expressions.*

import scala.collection.immutable.Map
import scala.reflect.{ClassTag, Enum}


trait Template[T] extends Element:
  // TODO: TemplateDef?
  def entity: Entity[T]

abstract class AbstractSensorDef[T](implicit jinjaType: Type[T]) extends Template[T]:
  // TODO: consider IntegrationSensorDef etc.

  // TODO: EntityDef
  def sensorType: String
  def name: String
  def state: Expr[T]
  def convertState(value: Expr[T]): Expr[_]
  def stateClass: Option[String]
  def entity: Entity[T] = Entity[T](haName(sensorType, name))
  def unit: Option[String]
  override def toStructure(context: Context): Any = Map(
    sensorType -> (
      Map(
      "name" -> name,
      "state" -> convertState(state).asCompleteJinja(context),
      ) ++
        optionalMap("unit_of_measurement", unit) ++
        optionalMap("state_class", stateClass)
    )
  )
  def variables: Set[Entity[_]] = state.variables
  override def defined: Set[Entity[_]] = Set(entity)

final case class RawTemplate[T](
  domain: String,
  name: String,
  rawDef: Map[String, Any],
  variables: Set[Entity[_]],
)(
  implicit jinjaType: Type[T]
) extends Template[T]:

  override def entity: Entity[T] = Entity(haName(domain, name))

  override def defined: Set[Entity[_]] = Set(entity)

  override def toStructure(context: Context): Any = Map(
    "name" -> name,
  ) ++ rawDef



final case class SensorDef[T](
  name: String,
  state: Expr[T],
  stateClass: Option[String] = None,
  unit: Option[String] = None,
)(implicit jinjaType: Type[T]) extends AbstractSensorDef[T]:
  override def convertState(value: Expr[T]): Expr[_] = jinjaType.serializeExpression(value)
  // TODO: SensorDef[Boolean]
  override def sensorType: String = "sensor"

final case class BinarySensorDef(
  name: String,
  state: Expr[Boolean],
  stateClass: Option[String] = None,
)(implicit jinjaType: Type[Boolean]) extends AbstractSensorDef[Boolean]:
  override def sensorType: String = "binary_sensor"
  override def convertState(value: Expr[Boolean]): Expr[_] = value
  override def unit: Option[String] = None

object BinarySensorDef:

  def selfReferencing(
    name: String,
    stateFactory: (Entity[Boolean] => Expr[Boolean]),
    stateClass: Option[String] = None,
  ): BinarySensorDef = BinarySensorDef(
    name = name, state = stateFactory(Entity[Boolean](haName("binary_sensor", name))), stateClass = stateClass
  )

  def fromEnum[T <: Enum](entity: Entity[T])(implicit tag: ClassTag[T]): Map[T, BinarySensorDef] =
    fromEnum(entity, entity.unprefixedName)

  def fromEnum[T <: Enum](expr: Expr[T], prefix: String)(implicit tag: ClassTag[T]): Map[T, BinarySensorDef] = {
    val values = tag.runtimeClass.getMethod("values").invoke(null).asInstanceOf[Array[T]]
    Map(
      (
        for (value <- values.toIndexedSeq) yield {
          value -> BinarySensorDef(
            name = s"${prefix} ${value.toString}",
            state = expr === Const(value),
          )
        }
      ) *
    )
  }
