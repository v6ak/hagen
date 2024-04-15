package com.v6ak.hagen.expressions.unsafe

import com.v6ak.hagen.expressions.{Context, ContextSafeSyntax, Entity, Expr, Transformer}

import scala.annotation.targetName

final class FuncCall[R] private[unsafe] (thisExprOption: Option[Expr[_]], name: String, namedParams: Map[String, Expr[_]], params: Seq[Expr[_]]) extends Expr[R] with ContextSafeSyntax:
  override def asJinja(context: Context): String = {
    val serializedPositionalParams = params.map(_.asJinja(context))
    val serializedNamedParams =  namedParams.map((name, expr) => s"$name=${expr.asJinja(context)}")
    val serializedAllParams = serializedPositionalParams ++ serializedNamedParams
    val objectPart = thisExprOption.fold("")(thisExpr => s"${thisExpr.asContextSafeJinja(context)}.")
    s"$objectPart$name(${serializedAllParams.mkString(", ")})"
  }

  override def variables: Set[Entity[_]] = (params ++ namedParams.values).flatMap(_.variables).toSet

  override def transform(transformer: Transformer): Expr[R] = transformer.transformBottomUp(
    new FuncCall[R](
      thisExprOption.map(thisExpr => transformer.transformTopDown(thisExpr).transform(transformer)),
      name,
      params = params.map(param => transformer.transformTopDown(param).transform(transformer)),
      namedParams = namedParams.view.mapValues(param => transformer.transformTopDown(param).transform(transformer)).toMap,
    )
  )



private class FuncCallBuilder(thisExprOption: Option[Expr[_]]):

  def apply[R](name: String, namedParams: Map[String, Expr[_]], params: Seq[Expr[_]]): FuncCall[R] = new FuncCall(thisExprOption, name, params=params, namedParams=namedParams)
  def apply[R](name: String): FuncCall[R] = new FuncCall(thisExprOption, name, params=Seq.empty, namedParams=Map.empty)
  @targetName("applyPositional")
  def apply[R](name: String, params: Expr[_]*): FuncCall[R] = apply(name, params=params, namedParams=Map.empty)
  @targetName("applyNamed")
  def apply[R](name: String, namedParams: (String, Expr[_])*): FuncCall[R] = apply(name, params=Seq(), namedParams=namedParams.toMap)

object FuncCall extends FuncCallBuilder(None):
  def onObject(expr: Expr[_]): FuncCallBuilder = FuncCallBuilder(Some(expr))
