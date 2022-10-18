package com.v6ak.hagen.expressions.unsafe

import com.v6ak.hagen.expressions.{Context, ContextSafeSyntax, Entity, Expr, Transformer}

final class FuncCall[R](name: String, params: Expr[_]*) extends Expr[R] with ContextSafeSyntax:
  override def asJinja(context: Context): String = s"$name(${params.map(_.asJinja(context)).mkString(", ")})"

  override def variables: Set[Entity[_]] = params.flatMap(_.variables).toSet

  override def transform(transformer: Transformer): Expr[R] = transformer.transformBottomUp(
    FuncCall[R](
      name,
      params.map(param => transformer.transformTopDown(param).transform(transformer))*,
    )
  )

