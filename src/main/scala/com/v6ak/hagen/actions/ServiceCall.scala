package com.v6ak.hagen.actions

import com.v6ak.hagen.materializeElements
import com.v6ak.hagen.expressions.{Context, Entity}

final case class ServiceCall(service: String, entity: Entity[_], data: Map[String, Any] = Map()) extends Action:
  override def toStructure(context: Context): Any = Map(
    "service" -> service,
    "target" -> Map(
      "entity_id" -> entity.name,
    ),
  ) ++ (if data.isEmpty then Map() else Map("data" -> materializeElements(context, data)))

  import com.v6ak.hagen.expressions.StringType

  override def variables: Set[Entity[_]] = Set(entity)
