package com.v6ak.hagen

import com.v6ak.hagen.expressions.{Entity, StateAttr}

import java.io.FileNotFoundException
import java.lang.reflect.Modifier
import java.nio.file.{Files, NoSuchFileException, Path}

private def extractDefinedItems(definedItems: Any): Set[Entity[_]] = {
  definedItems.getClass.getDeclaredMethods.toSeq
    .filter(m => (m.getModifiers & Modifier.STATIC) == 0)
    .filter(m => (m.getModifiers & Modifier.PUBLIC) != 0)
    .filter(_.getParameterCount == 0)
    .map(_.invoke(definedItems))
    .filter(!_.isInstanceOf[StateAttr[_]])
    .map(_.asInstanceOf[Entity[_]])
    .toSet
}


package object output {
  def output(f: String => Path, definedItems: Any, items: Map[String, Out]): Unit = {
    val existing = extractDefinedItems(definedItems)
    val newlyDefined = items.values.flatMap(_.defined).toSet
    val allDefined = existing ++ newlyDefined
    val used = items.values.flatMap(_.variables).toSet
    val missing = used.map(_.name) -- allDefined.map(_.name)
    val unused = allDefined.map(_.name) -- used.map(_.name)
    if missing.nonEmpty then
      throw RuntimeException(s"Some items are used, but aren't defined: $missing")

    var somethingChanged = false
    for ((name, content) <- items) {
      val out = f(name)
      val old = try{
        Some(Files.readString(out))
      }catch {
        case _: NoSuchFileException => None
      }
      if !old.contains(content.str) then {
        println(s"CHANGE: File $name changed.")
        somethingChanged = true
        Files.writeString(out, content.str)
      }
    }
    if (!somethingChanged) {
      println("NOTHING HAS CHANGED!")
    }
  }

}


