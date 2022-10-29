package com.v6ak.hagen.dashboards

import com.v6ak.hagen.Element
import com.v6ak.hagen.expressions.{Context, Entity}

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.file.{Files, Paths}
import scala.collection.immutable.Set

trait Icon extends Element{
  override final def toStructure(context: Context): Any = toStructure
  override def variables: Set[Entity[_]] = Set()
  override def defined: Set[Entity[_]] = Set()
  def toStructure: String
}

object Icon {
  def apply(name: String): Icon = RawIcon(name)
  // TODO: object Mdi ha:port/static/mdi/iconList.json
}

object IconMain:

  case class HAIconDescription(name: String, keywords: Seq[String])
  def main(args: Array[String]): Unit = {
    val Array(urlPrefix) = args
    val client = HttpClient.newHttpClient()
    val req = HttpRequest.newBuilder(URI.create(s"$urlPrefix/static/mdi/iconList.json")).build()
    val result = client.send(req, HttpResponse.BodyHandlers.ofString())
    import upickle.default.*
    implicit val iconReader: Reader[HAIconDescription] = macroRW[HAIconDescription]
    val names = read[Seq[HAIconDescription]](result.body()).map(_.name)
    val idsToNames = names.map { name =>
      val badChars = name.filterNot(c => c.isLetterOrDigit || Set('-').contains(c))
      if badChars.nonEmpty then throw RuntimeException(s"invalid name: $name; bad characters: $badChars")
      val nameParts = name.split("-")
      val scalaIdentifier = nameParts.map(_.capitalize).mkString("")
      scalaIdentifier -> name
    }.sorted
    // scalac generates too large bytecode for <clinit> and fails, so I've decided to use Java instead
    val javaLines = Seq(
      "package com.v6ak.hagen.dashboards;",
      "",
      "public class MdiIcons {",
    ) ++ idsToNames.map{ (javaIdentifier, name) =>
      s"""  public static Icon $javaIdentifier = Icon.apply("mdi:$name");"""
    } ++ Seq(
      "}"
    )
    Files.writeString(Paths.get("mdi-icons.java"), javaLines.map(_+"\n").mkString(""))
  }

private case class RawIcon(name: String) extends Icon:
  override def toStructure: String = name
