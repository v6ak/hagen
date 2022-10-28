package com.v6ak.hagen.expressions

object CurrentAutomation extends Expr[SwitchableEntity] {

  override def asJinja(context: Context): String = context.currentAutomation.get.asJinja(context)

  override def variables: Set[Entity[_]] = Set()

  override def transform(transformer: Transformer): Expr[SwitchableEntity] = this

}
