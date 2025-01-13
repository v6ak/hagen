package com.v6ak.hagen

trait Nameable[T]:
  def withName(name: String): T
