package com.v6ak.hagen.expressions

import com.v6ak.hagen.dashboards.Icon
import com.v6ak.hagen.expressions.{Const, Expr}
import com.v6ak.hagen.expressions.unsafe.*
import com.v6ak.jinja

trait Type[T]:
  /**
   * Converts value to jinja expression
   * @param value
   * @return
   */
  def const(value: T): String

  /**
   * Converts value to string representation in HA
   */
  def serialize(value: T): String

  def serializeExpression(value: Expr[T]): Expr[_]

  /**
   * Create Expr that converts expr to type T.
   * @param expr
   * @return
   */
  def convert(expr: Expr[String]): Expr[T]

implicit case object DoubleType extends Type[Double]:
  override def const(value: Double): String = value.toString
  override def convert(expr: Expr[String]): Expr[Double] = FilterExpr(expr, "float")
  override def serialize(value: Double): String = value.toString
  override def serializeExpression(value: Expr[Double]): Expr[_] = value

implicit case object IntType extends Type[Int]:
  override def const(value: Int): String = value.toString
  override def convert(expr: Expr[String]): Expr[Int] = FilterExpr(expr, "int")
  override def serialize(value: Int): String = value.toString
  override def serializeExpression(value: Expr[Int]): Expr[_] = value

implicit case object StringType extends Type[String]:
  override def const(value: String): String = jinja.string(value)
  override def convert(expr: Expr[String]): Expr[String] = expr
  override def serialize(value: String): String = value
  override def serializeExpression(value: Expr[String]): Expr[_] = value

implicit case object IconType extends Type[Icon]:
  override def const(value: Icon): String = jinja.string(value.toStructure)
  override def convert(expr: Expr[String]): Expr[Icon] = Reinterpret(expr)
  override def serialize(value: Icon): String = value.toStructure
  override def serializeExpression(value: Expr[Icon]): Expr[_] = value

implicit case object BooleanType extends Type[Boolean]:
  override def const(value: Boolean): String = if value then "true" else "false"
  override def convert(expr: Expr[String]): Expr[Boolean] = expr.matched[Boolean](
    Const("on") -> Const(true),
    Const("off") -> Const(false),
  )(
    defaultValue = UnexpectedValue()
  )
  override def serialize(value: Boolean): String = if value then "on" else "off"
  override def serializeExpression(value: Expr[Boolean]): Expr[String] = CondExpr(value, Const("on"), Const("off"))

final case class EnumType[T <: scala.reflect.Enum]() extends Type[T]:
  override def const(value: T): String = jinja.string(value.toString)
  override def convert(expr: Expr[String]): Expr[T] = Reinterpret(expr)
  override def serialize(value: T): String = value.toString
  override def serializeExpression(value: Expr[T]): Expr[_] = value

implicit def forEnum[T <: scala.reflect.Enum]: Type[T] = EnumType()
