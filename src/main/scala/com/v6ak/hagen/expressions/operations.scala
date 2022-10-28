package com.v6ak.hagen.expressions

import com.v6ak.hagen.expressions.BooleanOps.&&
import com.v6ak.hagen.expressions.{CondExpr, ConditionalBinOp, Const, ContextSafeSyntax, Entity, Expr, NonTransformable, Transformer}

import scala.annotation.targetName
import scala.reflect.ClassTag

// TODO: The value should be evaluated just once. This requires some deeper refactoring…
def Assign[T, U](value: Expr[T])(f: Expr[T] => Expr[U]): Expr[U] = {
  //println(f.getClass.getMethod("apply", Class.forName("com.v6ak.hagen.Expr")).getParameters()(0).getName)
  f(value)
}

def AssignSeq[T, U](value: Seq[Expr[T]])(f: Seq[Expr[T]] => Expr[U]): Expr[U] = {
  f(value)
}


object StringOps {
  import unsafe.*
  extension (e: Expr[String])
    def +(other: Expr[String]): Expr[String] = BinOp("+")(e, other)
}

object IntOps {
  import unsafe.*
  extension (e: Expr[Int])
    def +[T <: Int|Double](other: Expr[T]): Expr[T] = BinOp("+")(e, other)
    def *[T <: Int|Double](other: Expr[T]): Expr[T] = BinOp("*")(e, other)
    def /[T <: Int|Double](other: Expr[T]): Expr[Double] = BinOp("/")(e, other)
    def -(other: Expr[Int]): Expr[Int] = BinOp("-")(e, other)
    def >(other: Expr[Int]): Expr[Boolean] = BinOp(">")(e, other)
    def >=(other: Expr[Int]): Expr[Boolean] = BinOp(">=")(e, other)
    def <(other: Expr[Int]): Expr[Boolean] = BinOp("<")(e, other)
    def <=(other: Expr[Int]): Expr[Boolean] = BinOp("<=")(e, other)
}

object DoubleOps {
  import unsafe.*
  extension (e: Expr[Double])
    def +[T <: Int|Double](other: Expr[T]): Expr[T] = BinOp("+")(e, other)
    def *[T <: Int|Double](other: Expr[T]): Expr[T] = BinOp("*")(e, other)
    def -(other: Expr[Double]): Expr[Double] = BinOp("-")(e, other)
    def >(other: Expr[Double]): Expr[Boolean] = BinOp(">")(e, other)
    def >=(other: Expr[Double]): Expr[Boolean] = BinOp(">=")(e, other)
    def <(other: Expr[Double]): Expr[Boolean] = BinOp("<")(e, other)
    def <=(other: Expr[Double]): Expr[Boolean] = BinOp("<=")(e, other)
    def betweenInclusive(lower: Expr[Double], upper: Expr[Double]) = Assign(e)(value =>
      (lower <= value) && (value <= upper)
    )
}

object BooleanOps {
  import unsafe.*
  extension (e: Expr[Boolean])
    @targetName("and")
    def &&(other: Expr[Boolean]): Expr[Boolean] = And(e, other)
    @targetName("or")
    def ||(other: Expr[Boolean]): Expr[Boolean] = Or(e, other)
    def matches[T](ifTrue: Expr[T], ifFalse: Expr[T]): Expr[T] = CondExpr(e, ifTrue, ifFalse)
    def unary_! : Expr[Boolean] = UnOp("not")(e)
}

object EnumOps {
  import unsafe.*
  extension[T <: scala.reflect.Enum] (e: Expr[T])
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
      ???
    }
}
