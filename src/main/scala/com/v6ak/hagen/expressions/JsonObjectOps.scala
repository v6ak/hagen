package com.v6ak.hagen.expressions

final implicit class JsonObjectOps(e: Expr[JsonObject]) extends AnyVal :
  def getUnsafe[T <: Int | Double | String | JsonAny](name: String): Expr[T] = getUnsafe(Const(name))

  def getUnsafe[T <: Int | Double | String | JsonAny](name: Expr[String]): Expr[T] = unsafe.ArrayAccess(e, name)
