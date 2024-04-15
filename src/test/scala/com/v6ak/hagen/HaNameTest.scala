package com.v6ak.hagen

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class HaNameTest extends AnyFlatSpec with should.Matchers:

  "haName" should "handle spaces correctly" in {
    haName("foo", "bar gaz") should be("foo.bar_gaz")
  }

  "haName" should "handle dot correctly" in {
    haName("foo", "bar.gaz") should be("foo.bar_gaz")
  }

  "haName" should "handle mixed case correctly" in {
    haName("foo", "Bar_gAz") should be("foo.bar_gaz")
  }

  "haName" should "handle lower index correctly" in {
    haName("foo", "CO₂") should be("foo.co2")
  }
