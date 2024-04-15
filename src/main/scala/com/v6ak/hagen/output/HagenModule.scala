package com.v6ak.hagen.output

import com.v6ak.hagen.{Element, SensorDef, Template}
import com.v6ak.hagen.automation.Automation
import com.v6ak.hagen.expressions.{CounterDef, Entity, InputBooleanDef, UtilityMeterDef}
import com.v6ak.{HeteroKey, HeteroMap}

import scala.collection.immutable.Map
import scala.reflect.ClassTag

trait HagenKey[T] extends HeteroKey[T]:
  def mergeValues(a: T, b: T): T

trait SeqHagenKey[T] extends HagenKey[Seq[T]]:
  override def mergeValues(a: Seq[T], b: Seq[T]): Seq[T] = a ++ b

trait MapHagenKey[K, V] extends HagenKey[Map[K, V]]:
  override def mergeValues(a: Map[K, V], b: Map[K, V]): Map[K, V] = {
    val intersection = a.keySet.intersect(b.keySet)
    if (intersection.nonEmpty) {
      throw Exception(s"Intersecting keys for $this: $intersection")
    }
    a ++ b
  }

trait SingleHagenKey[T] extends HagenKey[T]:
  override def mergeValues(a: T, b: T): T =
    throw RuntimeException(
      s"Cannot merge value $a with $b for $this. Operation is not supported. There can be at most one key."
    )

object TopLevelKeys extends MapHagenKey[String, Element] {}
object Automations extends SeqHagenKey[Automation] {}
object InputBooleans extends SeqHagenKey[InputBooleanDef] {}
object UtilityMeters extends SeqHagenKey[UtilityMeterDef[_]] {}
object Sensors extends SeqHagenKey[SensorDef[_]] {}
object Templates extends SeqHagenKey[Template[_]] {}
object Counters extends SeqHagenKey[CounterDef] {}
object Updatables extends SeqHagenKey[Updatable] {}

final case class SingleObject[K, T](key: K)(implicit classTag: ClassTag[T]) extends SingleHagenKey[T] {}

trait HagenModule:
  def dependencies: Set[HagenKey[_]]
  def produces: Set[HagenKey[_]]
  def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]]

trait SimpleHagenModule extends HagenModule:
  def content: HeteroMap[HagenKey[_]]
  final override def dependencies: Set[HagenKey[_]] = Set.empty
  final override def produces: Set[HagenKey[_]] = content.keySet
  final override def content(params: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] = {
    content
  }


final case class State(params: HeteroMap[HagenKey[_]], keysUsed: Set[HagenKey[_]])

object State:
  val Initial = State(HeteroMap(), Set())


object ModuleEvaluation:

  def evalDependentModules(modules: Seq[HagenModule]): State = {
    val requirementsToModules: Map[HagenKey[_], Set[HagenModule]] =
      modules.flatMap(module => module.produces.map(key => (module, key))).
        groupBy(_._2).
        view.mapValues(_.map(_._1).toSet).toMap
    val dependencies: Map[HagenModule, Set[HagenModule]] =
      modules.
        map(module =>
          module -> module.dependencies.flatMap(key => requirementsToModules(key))
        ).
        toMap

    def plan(modulesDone: Set[HagenModule], modulesToEval: Set[HagenModule]): Seq[HagenModule] = {
      if modulesToEval.isEmpty then
        Seq()
      else {
        val evaluable = modulesToEval.filter(module => (dependencies(module) -- modulesDone).isEmpty)
        if evaluable.isEmpty then {
          throw RuntimeException(s"Cannot make any progress, there seems to be some cyclic dependency." +
            s"\nmodulesDone=$modulesDone\nmodulesToEval=$modulesToEval"
          )
        }
        val evaluableSeq = evaluable.toSeq.sortBy(_.toString) // Make the order deterministic
        println(s"evaluableSeq (${evaluableSeq.size}) = $evaluableSeq")
        evaluableSeq ++ plan(modulesDone ++ evaluable, modulesToEval -- evaluable)
      }
    }

    val moduleSequence = plan(Set(), modules.toSet)
    println(s"moduleSequence = $moduleSequence")
    evalModuleSequence(moduleSequence)
  }

  private def evalModuleSequence(values: Seq[HagenModule]): State = {
    values.foldLeft[State](State.Initial)((state: State, module: HagenModule) => {
      val params = state.params.filteredKeys(module.dependencies)
      val newKeysUsed = state.keysUsed ++ params.keySet

      val res = module.content(params)

      if module.produces != res.keySet then {
        throw RuntimeException(s"Module $module claimed to produce ${module.produces}, but actually produced ${res.keySet}")
      }
      val usedKeysUpdated = res.keySet.intersect(newKeysUsed)
      if usedKeysUpdated.nonEmpty then {
        throw new RuntimeException(s"Attempted to update already used keys: $usedKeysUpdated")
      }

      State(
        params = mergeHeteroMaps(state, res),
        keysUsed = newKeysUsed
      )
    })
  }

  private def mergeHeteroMaps(state: State, b: HeteroMap[HagenKey[_]]): HeteroMap[HagenKey[_]] = {
    val allKeys = state.params.keySet ++ b.keySet

    def mergeKeys[T](key: HagenKey[T]) = {
      val value = (state.params.get(key), b.get(key)) match {
        case (None, None) => throw RuntimeException(s"WTF $key")
        case (None, Some(bval)) => bval
        case (Some(aval), None) => aval
        case (Some(aval), Some(bval)) => key.mergeValues(aval, bval)
      }
      key -> value
    }

    HeteroMap(
      allKeys.map(mergeKeys).map { case (k, v) => k -> v }.toSeq *
    )
  }


enum UpdateState(override val toString: String):
  case Idle extends UpdateState("idle")

final case class Updatable(id: String, name: String, deviceType: String):
  import com.v6ak.hagen.expressions.forEnum
  import com.v6ak.hagen.expressions.*
  def updateEntity: Entity[String] = Entity(s"update.$id")
  def updateAvailable: Entity[Boolean] = Entity[Boolean](s"binary_sensor.${id}_update_available")
  def updateState: Entity[UpdateState] = Entity[UpdateState](s"sensor.${id}_update_state")
