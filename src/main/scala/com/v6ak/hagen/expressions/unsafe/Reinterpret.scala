package com.v6ak.hagen.expressions.unsafe

import com.v6ak.hagen.expressions.{Context, Entity, Expr, Transformer}

final class Reinterpret[F, T](a: Expr[F]) extends Expr[T]:

  override def asJinja(context: Context): String = a.asJinja(context)

  override def asContextSafeJinja(context: Context): String = a.asContextSafeJinja(context)

  override def transform(transformer: Transformer): Expr[T] = transformer.transformBottomUp(
    Reinterpret(
      transformer.transformTopDown(a).transform(transformer)
    )
  )

  override def variables: Set[Entity[_]] = a.variables
