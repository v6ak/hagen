package com.v6ak.hagen.expressions
import com.v6ak.hagen.Element

trait Expr[T] extends Element:

  def asJinja(context: Context): String

  def asContextSafeJinja(context: Context): String = s"(${asJinja(context)})"

  def asCompleteJinja(context: Context): String = s"{{ ${asJinja(context)} }}"

  def ===(other: Expr[T]): Expr[Boolean] = unsafe.BinOp("==")(this, other)

  def in(others: Expr[T]*): Expr[Boolean] = Assign(this)(self => Or.of(others.map(self === _)))
  
  def !==(other: Expr[T]): Expr[Boolean] = unsafe.BinOp("!=")(this, other)

  def transform(transformer: Transformer): Expr[T]

  def variables: Set[Entity[_]]

  override def toStructure(context: Context): Any = asCompleteJinja(context)

  override def defined: Set[Entity[_]] = Set()

  def matched[O](options: (Expr[T], Expr[O])*)(defaultValue: Expr[O]): Expr[O] = {
    Assign(this)(entityValue =>
      options.foldLeft(defaultValue) {
        case (notMatched, (inp: Expr[T], out: Expr[O])) => (entityValue === inp).matches(
          ifTrue = out,
          ifFalse = notMatched
        )
      }
    )
  }
