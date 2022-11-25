package com.v6ak.hagen.expressions.unsafe
import com.v6ak.hagen.expressions.*

class ArrayAccess[I, K, O](arrayLike: Expr[I], key: Expr[K]) extends Expr[O]:

  override def variables: Set[Entity[_]] = arrayLike.variables ++ key.variables

  override def transform(transformer: Transformer): Expr[O] = transformer.transformBottomUp(
    ArrayAccess(
      arrayLike = transformer.transformTopDown(arrayLike).transform(transformer),
      key = transformer.transformTopDown(key).transform(transformer),
    )
  )

  override def asJinja(context: Context): String = s"${arrayLike.asContextSafeJinja(context)}[${key.asJinja(context)}]"


