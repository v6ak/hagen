package com.v6ak.hagen.expressions

object Entity {
  def apply[T](name: String)(implicit jinjaType: Type[T]): Entity[T] = SimpleEntity(name)
}

abstract class Entity[T]()(implicit jinjaType: Type[T]) extends ContextDependentEntity[T]():
  // TODO: def attr
  def name: String

  override def getName(context: Context): String = name

  def unprefixedName: String = name.dropWhile(_ != '.').drop(1)

  override def variables: Set[Entity[_]] = Set(this)

  def baseName: String = name.dropWhile(_!='.').drop(1)
