package com.v6ak.hagen

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

final class JinjaStringTest extends AnyFlatSpec with should.Matchers:
  "jinja.string" should "correctly quote simple string" in {
    com.v6ak.jinja.string("hello") should be(""" "hello" """.trim)
  }

  "jinja.string" should "correctly quote a quote" in {
    com.v6ak.jinja.string("hell\"o") should be(""" "hell\"o" """.trim)
  }

  "jinja.string" should "correctly quote a backslash" in {
    com.v6ak.jinja.string("hell\\o") should be(""" "hell\\o" """.trim)
  }

  "jinja.string" should "correctly quote a backslash and quote" in {
    com.v6ak.jinja.string("hell\\\"o") should be(""" "hell\\\"o" """.trim)
  }

