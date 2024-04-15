package com.v6ak.hagen

import com.v6ak.hagen.expressions.{Context, Entity}

trait Element:
  def toStructure(context: Context): Any
  def variables: Set[Entity[?]]
  def defined: Set[Entity[?]]

trait NakeableElement extends Element:
  def toInnerStructure(context: Context): Any

trait TopLevelElement extends Element:
  override def toStructure(context: Context): Map[String, Element]

trait TupleElement extends Element:
  override def toStructure(context: Context): (String, Map[?, ?])

trait DelegatingElement extends Element:
  def subElements: Set[Element]
  def variables: Set[Entity[?]] = subElements.flatMap(_.variables)
  def defined: Set[Entity[?]] = subElements.flatMap(_.defined)

final case class RawElement(structure: Any, variables: Set[Entity[?]], defined: Set[Entity[?]]) extends Element:
  override def toStructure(context: Context): Any = structure


final case class MapTopLevelElement(structure: Map[String, Element]) extends TopLevelElement:

  override def variables: Set[Entity[?]] = structure.values.flatMap(_.variables).toSet

  override def defined: Set[Entity[?]] = structure.values.flatMap(_.defined).toSet

  override def toStructure(context: Context): Map[String, Element] = structure

final case class NakedElement(inner: NakeableElement) extends Element{
  override def toStructure(context: Context): Any = inner.toInnerStructure(context)

  override def variables: Set[Entity[?]] = inner.variables

  override def defined: Set[Entity[?]] = inner.defined
}