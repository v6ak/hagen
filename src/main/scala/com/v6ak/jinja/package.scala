package com.v6ak

package object jinja {
  def string(s: String): String = {
    if ((s contains '\\')) {
      throw RuntimeException("Escaping not fully implemented")
    }
    s"\"${s.replace("\"", "\\\"")}\""
  }
}
