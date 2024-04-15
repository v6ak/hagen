package com.v6ak.hagen.actions

import com.v6ak.hagen.materializeElements
import com.v6ak.hagen.expressions.{Context, Entity}

final case class ServiceCall(service: String, entities: Seq[Entity[_]], data: Map[String, Any] = Map()) extends Action:
  override def toStructure(context: Context): Any = {
    val entityNames = entities.map(_.name)
    val entityIdField = entityNames match
      case Seq(single) => single
      case other => other
    val dataMap = if data.isEmpty
      then Map()
      else Map("data" -> materializeElements(context, data))
    Map(
      "service" -> service,
      "target" -> Map(
        "entity_id" -> entityIdField,
      ),
    ) ++ dataMap
  }

  import com.v6ak.hagen.expressions.StringType

  override def variables: Set[Entity[_]] = entities.toSet // TODO: data?

object ServiceCall:

  def apply(service: String, entity: Entity[_]): ServiceCall = new ServiceCall(
    service = service,
    entities = Seq(entity),
    data = Map(),
  )
  def apply(service: String, entity: Entity[_], data: Map[String, Any]): ServiceCall = new ServiceCall(
    service = service,
    entities = Seq(entity),
    data = data,
  )
