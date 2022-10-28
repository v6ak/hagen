package com.v6ak.hagen.expressions

object CurrentAutomationName extends Expr[String] {

  override def asJinja(context: Context): String = Const(context.currentAutomation.get.name).asJinja(context)

  override def variables: Set[Entity[_]] = Set()

  override def transform(transformer: Transformer): Expr[String] = this

}
