package com.v6ak.hagen.expressions

// TODO: find a better type
object CurrentAutomationEntity extends ContextDependentEntity[String] with NonTransformable[String] :
  val LastTriggered: StateAttr[Instant] = StateAttr(this, "last_triggered")
  val LastTriggeredOrEpochStart: Expr[Instant] = unsafe.BinOp("or")(
    LastTriggered,
    Const(0.0).asDatetime
  )


  override def getName(context: Context): String = context.currentAutomation.get.name

  override def variables: Set[Entity[_]] = Set()
