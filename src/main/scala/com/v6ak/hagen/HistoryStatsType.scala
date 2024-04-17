package com.v6ak.hagen

import com.v6ak.hagen.expressions.*

import scala.concurrent.duration.Duration

enum HistoryStatsType[T](val name: String)(implicit val jinjaType: Type[T]):
  override def toString: String = name

  case Time extends HistoryStatsType[Duration]("time")
  case Ratio extends HistoryStatsType[Double]("ratio")
  case Count extends HistoryStatsType[Int]("count")
