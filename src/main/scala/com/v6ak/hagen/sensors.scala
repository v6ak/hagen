package com.v6ak.hagen

import com.v6ak.hagen.dashboards.Icon
import com.v6ak.hagen.expressions.*
import com.v6ak.hagen.output.{HagenKey, Sensors, Templates}

import scala.collection.immutable.Map
import scala.reflect.{ClassTag, Enum}

trait EntityDef[E <: Entity[?], K] extends Element:
  def entity: E
  def key: HagenKey[K]
  def createHagenDefinition: K

trait SimpleEntityDef[T, K] extends EntityDef[Entity[T], K]

abstract class GenericSensorDef[T] extends SimpleEntityDef[T, Seq[GenericSensorDef[?]]]:
  def entity: Entity[T]

  override def key: HagenKey[Seq[GenericSensorDef[?]]] = Sensors

  override def createHagenDefinition: Seq[GenericSensorDef[?]] = Seq(this)


abstract class TemplateSensorDef[T](implicit jinjaType: Type[T]) extends SimpleEntityDef[T, Seq[TemplateSensorDef[?]]]:
  def sensorType: String
  def name: String
  def state: Expr[T]
  def icon: Option[Entity[T] => Expr[Icon]]
  def convertState(value: Expr[T]): Expr[?]
  def stateClass: Option[String]
  def deviceClass: Option[String]
  def entity: Entity[T] = Entity[T](haName(sensorType, name))
  def unit: Option[String]
  override def key: HagenKey[Seq[TemplateSensorDef[?]]] = Templates
  override def createHagenDefinition: Seq[TemplateSensorDef[?]] = Seq(this)
  override def toStructure(context: Context): Any = Map(
    sensorType -> (
      Map(
      "name" -> name,
      "state" -> convertState(state).asCompleteJinja(context),
      ) ++
        optionalMap("unit_of_measurement", unit) ++
        optionalMap("state_class", stateClass) ++
        optionalMap("device_class", deviceClass) ++
        optionalMap("icon", icon.map(factory => factory(entity).toStructure(context)))
    )
  )
  def variables: Set[Entity[?]] = state.variables
  override def defined: Set[Entity[?]] = Set(entity)

final case class RawSensorDef[T](
  domain: String,
  name: String,
  rawDef: Map[String, Any],
  variables: Set[Entity[?]],
)(
  implicit jinjaType: Type[T]
) extends GenericSensorDef[T]:

  override def entity: Entity[T] = Entity(haName(domain, name))

  override def defined: Set[Entity[?]] = Set(entity)

  override def toStructure(context: Context): Any = Map(
    "name" -> name,
  ) ++ rawDef



final case class SensorDef[T](
  name: String,
  state: Expr[T],
  stateClass: Option[String] = None,
  deviceClass: Option[String] = None,
  unit: Option[String] = None,
  icon: Option[Entity[T] => Expr[Icon]] = None,
)(implicit jinjaType: Type[T]) extends TemplateSensorDef[T]:
  override def convertState(value: Expr[T]): Expr[?] = jinjaType.serializeExpression(value)
  override def sensorType: String = "sensor"

final case class BinarySensorDef(
  name: String,
  state: Expr[Boolean],
  stateClass: Option[String] = None,
  deviceClass: Option[String] = None,
  icon: Option[Entity[Boolean] => Expr[Icon]] = None,
)(implicit jinjaType: Type[Boolean]) extends TemplateSensorDef[Boolean]:
  override def sensorType: String = "binary_sensor"
  override def convertState(value: Expr[Boolean]): Expr[?] = value
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
