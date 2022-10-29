package com.v6ak.hagen.output

import org.snakeyaml.engine.v2.api.{Dump, DumpSettings, RepresentToNode}
import org.snakeyaml.engine.v2.common.ScalarStyle
import org.snakeyaml.engine.v2.nodes.{Node, ScalarNode, Tag}
import org.snakeyaml.engine.v2.representer.StandardRepresenter

import scala.reflect.ClassTag

case class Input(id: String)

case class Include(id: String)

object Yaml:
  def serialize(data: Any): String = {
    val settings = DumpSettings.builder()
      //.setMultiLineFlow(true)
      .setDefaultScalarStyle(ScalarStyle.DOUBLE_QUOTED)
      .build();
    val representer = ScalaStandardRepresenter(settings)
    val dump = Dump(settings, representer)
    dump.dumpToString(data)
  }

final class MappedRepresenter[T](next: RepresentToNode)(f: T => Any) extends RepresentToNode:
  override def representData(data: Any): Node = next.representData(f(data.asInstanceOf[T]))

final class ScalaStandardRepresenter(settings: DumpSettings) extends StandardRepresenter(settings):
  def addMapping[F, T](f: F => T)(implicit fromTag: ClassTag[F], toTag: ClassTag[T]): Unit = {
    val nextRepresenter = parentClassRepresenters.get(toTag.runtimeClass)
    if (nextRepresenter == null) {
      throw NullPointerException()
    }
    parentClassRepresenters.put(fromTag.runtimeClass, MappedRepresenter(nextRepresenter)(f))
  }
  def addScalarTagMapping[T](tag: Tag, f: T => String)(implicit classTag: ClassTag[T]): Unit = {
    representers.put(classTag.runtimeClass, obj => {
      new ScalarNode(tag, f(obj.asInstanceOf[T]), ScalarStyle.SINGLE_QUOTED)
    })
  }

  import scala.jdk.CollectionConverters.*
  addMapping[Map[_, _], java.util.Map[_, _]](_.asJava)
  addMapping[Seq[_], java.util.List[_]](_.asJava)
  addMapping[Set[_], java.util.Set[_]](_.asJava)
  addScalarTagMapping[Input](new Tag("!input"), _.id)
  addScalarTagMapping[Include](new Tag("!include"), _.id)
