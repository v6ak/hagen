package com.v6ak.hagen.expressions

object Conditions:
  
  def of[T](conditions: Iterable[(Expr[Boolean], Expr[T])], ifNoneMatches: Expr[T]): Expr[T] =
    val (firstCondition, firstValue) = conditions.head
    firstCondition.matches[T](
      ifTrue = firstValue,
      ifFalse = conditions.view.tail match
        case x if x.isEmpty => ifNoneMatches
        case otherOptions => of(otherOptions, ifNoneMatches)
    )
    
  def firstMatching[T](conditions: (Expr[Boolean], Expr[T])*)(ifNoneMatches: Expr[T]): Expr[T] =
    of(conditions, ifNoneMatches)
