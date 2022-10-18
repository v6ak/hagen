package com.v6ak

import com.v6ak
import com.v6ak.hagen.output.HagenKey

trait HeteroKey[+V] {}

final class HeteroMap[K <: HeteroKey[_]] private (map: Map[K, Any]):

  def get[T <: K & HeteroKey[O], O](key: T): Option[O] = map.get(key).map(_.asInstanceOf[O])
  def keySet: Set[K] = map.keySet
  def apply[T <: K & HeteroKey[O], O](key: T): O = get(key).get
  def contains(key: K): Boolean = map.contains(key)
  def filteredKeys(keys: Set[K]): HeteroMap[K] = HeteroMap(keys.map(key => key -> this(key)).toSeq*)

  override def toString: String = s"HeteroMap($map)"


object HeteroMap:
  def apply[K <: HeteroKey[T], T](pairs: HeteroPair[K, T]*): HeteroMap[K] = new HeteroMap(
    Map(pairs*)
  )

type HeteroPair[+K <: HeteroKey[T], +T] = (K, T)


// TODO: test static typing