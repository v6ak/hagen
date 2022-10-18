package com.v6ak.hagen.output

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.{Context, Entity}

trait Out {
  def variables: Set[Entity[_]]

  def defined: Set[Entity[_]]

  def str: String
}


final class YamlOut(content: Seq[Element], indent: String = "") extends Out:
  override def variables: Set[Entity[_]] = content.flatMap(_.variables).toSet
  override def defined: Set[Entity[_]] = content.flatMap(_.defined).toSet
  override def str: String = content.map(a => Yaml.serialize(a.toStructure(Context.TemporaryHack))).mkString("\n").
    split("\n").map(indent + _).mkString("\n")
