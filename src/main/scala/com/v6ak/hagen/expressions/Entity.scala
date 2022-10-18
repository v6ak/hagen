package com.v6ak.hagen.expressions

object Entity {
  def apply[T](name: String)(implicit jinjaType: Type[T]): Entity[T] = SimpleEntity(name)
}

abstract class Entity[T]()(implicit val jinjaType: Type[T]) extends DelegateExpr[T] with NonTransformable[T]:
  // TODO: def attr
  def name: String

  def unprefixedName: String = name.dropWhile(_ != '.').drop(1)

  override def variables: Set[Entity[_]] = Set(this)

  //override def toString: String = s"Entity($name)"
  protected def delegate = jinjaType.convert(
    unsafe.FuncCall("states", Const(name))
  )

  def baseName: String = name.dropWhile(_!='.').drop(1)
