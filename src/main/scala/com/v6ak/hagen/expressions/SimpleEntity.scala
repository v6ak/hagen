package com.v6ak.hagen.expressions

final case class SimpleEntity[T] private[expressions](name: String)(implicit jinjaType: Type[T]) extends Entity[T]() {}
