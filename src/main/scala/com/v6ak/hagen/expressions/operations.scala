package com.v6ak.hagen.expressions

import com.v6ak.hagen.expressions.{CondExpr, ConditionalBinOp, Const, ContextSafeSyntax, Entity, Expr, NonTransformable, Transformer}

import scala.annotation.targetName
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

// TODO: The value should be evaluated just once. This requires some deeper refactoring…
def Assign[T, U](value: Expr[T])(f: Expr[T] => Expr[U]): Expr[U] = {
  //println(f.getClass.getMethod("apply", Class.forName("com.v6ak.hagen.Expr")).getParameters()(0).getName)
  f(value)
}

implicit class MonadicAssignment[T](val value: Expr[T]):
  def map[U](f: Expr[T] => Expr[U]): Expr[U] = Assign(value)(f)
  def flatMap[U](f: Expr[T] => Expr[U]): Expr[U] = Assign(value)(f)

def AssignSeq[T, U](value: Seq[Expr[T]])(f: Seq[Expr[T]] => Expr[U]): Expr[U] = {
  f(value)
}


implicit final class StringOps(e: Expr[String]) extends AnyVal:
  import unsafe.*
  def +(other: Expr[String]): Expr[String] = BinOp("+")(e, other)
  def asDatetime: Expr[Instant] = FilterExpr(e, "as_datetime")

trait OrderingOps[T] extends Any:
  import unsafe.*
  protected def e: Expr[T]
  def >(other: Expr[T]): Expr[Boolean] = BinOp(">")(e, other)
  def >=(other: Expr[T]): Expr[Boolean] = BinOp(">=")(e, other)
  def <(other: Expr[T]): Expr[Boolean] = BinOp("<")(e, other)
  def <=(other: Expr[T]): Expr[Boolean] = BinOp("<=")(e, other)


implicit final class Stringifiable[T <: Int|Double](protected val e: Expr[T]) extends AnyVal:
  import unsafe.*
  def toStr: Expr[String] = FilterExpr(e, "string")

implicit final class IntOps(protected val e: Expr[Int]) extends AnyVal with OrderingOps[Int]:
  import unsafe.*
  def +[T <: Int|Double](other: Expr[T]): Expr[T] = BinOp("+")(e, other)
  def *[T <: Int|Double](other: Expr[T]): Expr[T] = BinOp("*")(e, other)
  def /[T <: Int|Double](other: Expr[T]): Expr[Double] = BinOp("/")(e, other)
  def -[T <: Int|Double](other: Expr[T]): Expr[T] = BinOp("-")(e, other)

  def atLeast(other: Expr[Int]): Expr[Int] = max(e, other)
  def atMost(other: Expr[Int]): Expr[Int] = min(e, other)


implicit final class DoubleOps(protected val e: Expr[Double]) extends AnyVal with OrderingOps[Double]:
  import unsafe.*
  def +[T <: Int|Double](other: Expr[T]): Expr[Double] = BinOp("+")(e, other)
  def *[T <: Int|Double](other: Expr[T]): Expr[Double] = BinOp("*")(e, other)
  def /[T <: Int|Double](other: Expr[T]): Expr[Double] = BinOp("/")(e, other)
  def %[T <: Int|Double](other: Expr[T]): Expr[Double] = BinOp("%")(e, other)
  def -[T <: Int|Double](other: Expr[T]): Expr[Double] = BinOp("-")(e, other)
  def ^[T <: Int|Double](other: Expr[T]): Expr[Double] = BinOp("**")(e, other)
  def ^[T <: Int|Double](other: T)(implicit jinjaPowType: Type[T]): Expr[Double] = BinOp("**")(e, Const(other))

  def betweenInclusive(lower: Expr[Double], upper: Expr[Double]) = Assign(e)(value =>
    (lower <= value) && (value <= upper)
  )
  def betweenDegreesInclusive(lower: Expr[Double], upper: Expr[Double]) =
    for
      value <- normalizeAngleDeg(e)
      lower <- normalizeAngleDeg(lower)
      upper <- normalizeAngleDeg(upper)
    yield
      Or(
        (lower <= value) && (value <= upper), // 0 <= lower <= value <= upper (mod 360)
        (upper < lower) && (lower <= value), // lower <= 0 <= value <= upper (mod 360)
        (upper < lower) && (value <= upper), // lower <= value <= 0 <= upper (mod 360)
      )

  def unary_- : Expr[Double] = UnOp("-")(e)

  def asDatetime: Expr[Instant] = FilterExpr(e, "as_datetime")

  def ceil: Expr[Double] = FilterExpr(e, FuncCall("round", Const(0), Const("ceil")))
  def floor: Expr[Double] = FilterExpr(e, FuncCall("round", Const(0), Const("floor")))
  def toInt: Expr[Int] = FilterExpr(e, "int")

  def atLeast(other: Expr[Double]): Expr[Double] = max(e, other)
  def atMost(other: Expr[Double]): Expr[Double] = min(e, other)

implicit final class BooleanOps(e: Expr[Boolean]) extends AnyVal:
  import unsafe.*
  @targetName("and")
  def &&(other: Expr[Boolean]): Expr[Boolean] = And(e, other)
  @targetName("or")
  def ||(other: Expr[Boolean]): Expr[Boolean] = Or(e, other)
  def matches[T](ifTrue: Expr[T], ifFalse: Expr[T]): Expr[T] = CondExpr(e, ifTrue, ifFalse)
  def unary_! : Expr[Boolean] = UnOp("not")(e)


implicit final class EnumOps[T <: scala.reflect.Enum] (e: Expr[T]) extends AnyVal:
  import unsafe.*
  def asString: Expr[String] = Reinterpret(e)
  def matches[U](options: (T, Expr[U])*)(implicit tag: ClassTag[T]): Expr[U] = {
    val keys = options.map(_._1)
    val keyMap = keys.groupBy(identity).view.mapValues(_.size).toMap
    val duplicateKeys = keyMap.filter(_._2 > 1).keys
    if (duplicateKeys.nonEmpty) {
      throw RuntimeException(s"Duplicate keys: $duplicateKeys")
    }
    val keySet = keyMap.keySet
    val expectedKeySet = tag.runtimeClass.getMethod("values").invoke(null).asInstanceOf[Array[T]].toSet
    val missingKeys = expectedKeySet -- keySet
    if (missingKeys.nonEmpty) {
      throw RuntimeException(s"Missing keys: $missingKeys")
    }
    if (keySet != expectedKeySet) {
      throw RuntimeException(s"keySet != expectedKeySet: $keySet != $expectedKeySet")
    }
    e.matched(options.map((key, value) => Const(key) -> value)*)(UnexpectedValue())
  }

implicit final class DateTimeOps[T <: Instant|NaiveDateTime](protected val e: Expr[T]) extends OrderingOps[T]: // extends AnyVal: https://github.com/lampepfl/dotty/issues/16464
  import unsafe.*
  @targetName("minusDateTime")
  def -(other: Expr[T]): Expr[Duration] = BinOp("-")(e, other)
  @targetName("minusDuration")
  def -(other: Expr[Duration]): Expr[T] = BinOp("-")(e, other)
  def +(other: Expr[Duration]): Expr[T] = BinOp("+")(e, other)

  def timestamp: Expr[Int] = FuncCall.onObject(e)("timestamp")

implicit final class DurationOps(protected val e: Expr[Duration]) extends AnyVal with OrderingOps[Duration]:
  import unsafe.*
  def microseconds: Expr[Int] = FieldExpr(e, "microseconds")
  def seconds: Expr[Int] = FieldExpr(e, "seconds")

  def /(other: Expr[Duration]): Expr[Double] = BinOp("/")(e, other)
