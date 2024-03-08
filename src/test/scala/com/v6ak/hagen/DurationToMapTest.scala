package com.v6ak.hagen

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.concurrent.duration
import scala.concurrent.duration.Duration

final class DurationToMapTest extends AnyFlatSpec with should.Matchers {
  "durationToMap" should "correctly serialize seconds" in {
    durationToMap(Duration(1, duration.SECONDS)) should be(Map("seconds" -> 1))
    durationToMap(Duration(10, duration.SECONDS)) should be(Map("seconds" -> 10))
    durationToMap(Duration(50, duration.SECONDS)) should be(Map("seconds" -> 50))
  }
  "durationToMap" should "correctly serialize minutes" in {
    durationToMap(Duration(1, duration.MINUTES)) should be(Map("minutes" -> 1))
    durationToMap(Duration(10, duration.MINUTES)) should be(Map("minutes" -> 10))
    durationToMap(Duration(50, duration.MINUTES)) should be(Map("minutes" -> 50))
  }
  "durationToMap" should "correctly serialize hours" in {
    durationToMap(Duration(1, duration.HOURS)) should be(Map("hours" -> 1))
    durationToMap(Duration(10, duration.HOURS)) should be(Map("hours" -> 10))
    durationToMap(Duration(23, duration.HOURS)) should be(Map("hours" -> 23))
  }
  "durationToMap" should "correctly serialize days" in {
    durationToMap(Duration(1, duration.DAYS)) should be(Map("days" -> 1))
    durationToMap(Duration(10, duration.DAYS)) should be(Map("days" -> 10))
    durationToMap(Duration(400, duration.DAYS)) should be(Map("days" -> 400))
  }
  "durationToMap" should "correctly handle overflows" in {
    durationToMap(Duration(60, duration.SECONDS)) should be(Map("minutes" -> 1))
    durationToMap(Duration(60, duration.MINUTES)) should be(Map("hours" -> 1))
    durationToMap(Duration(24, duration.HOURS)) should be(Map("days" -> 1)) // are we sure about the behavior on DST change?
  }
  "durationToMap" should "correctly handle multiunit" in {
    durationToMap(Duration(61, duration.SECONDS)) should be(Map("minutes" -> 1, "seconds" -> 1))
    durationToMap(Duration(61, duration.MINUTES)) should be(Map("hours" -> 1, "minutes" -> 1))
    durationToMap(Duration(25, duration.HOURS)) should be(Map("days" -> 1, "hours" -> 1)) // are we sure about the behavior on DST change?
  }
  "durationToMap" should "correctly serialize sub-second units" in {
    durationToMap(Duration(1, duration.NANOSECONDS)) should be(Map("seconds" -> 1e-9))
    durationToMap(Duration(1, duration.MICROSECONDS)) should be(Map("seconds" -> 1e-6))
    durationToMap(Duration(1, duration.MILLISECONDS)) should be(Map("seconds" -> 1e-3))
  }
  "durationToMap" should "correctly serialize zero" in {
    val Zero = Map("seconds" -> 0)
    durationToMap(Duration(0, duration.NANOSECONDS)) should be(Zero)
    durationToMap(Duration(0, duration.MICROSECONDS)) should be(Zero)
    durationToMap(Duration(0, duration.MILLISECONDS)) should be(Zero)
    durationToMap(Duration(0, duration.SECONDS)) should be(Zero)
    durationToMap(Duration(0, duration.MINUTES)) should be(Zero)
    durationToMap(Duration(0, duration.HOURS)) should be(Zero)
    durationToMap(Duration(0, duration.DAYS)) should be(Zero)
  }
}

