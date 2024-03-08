package com.v6ak

package object jinja {

  private val StringReplacements = Map(
    '\\' -> "\\\\",
    '\"' -> "\\\"",
  ).withDefault(_.toString)
  def string(s: String): String = {
    s"\"${s.flatMap(StringReplacements)}\""
  }
}
