package com.v6ak.hagen.examples

import com.v6ak.hagen.RawElement
import com.v6ak.hagen.addons.breakingChanges.BreakingChangesConfig
import com.v6ak.hagen.dashboards.{DashboardPages, DashboardParts}
import com.v6ak.hagen.examples.DefinedItems.{fridgeHumiditySensor, fridgeTemperatureSensor, rpiPowerStatus}
import com.v6ak.hagen.extensions.batteryPowered.BatteryPoweredItems
import com.v6ak.hagen.extensions.fridge.{FridgeDashboard, FridgeModule}
import com.v6ak.hagen.extensions.highlights.*
import com.v6ak.hagen.hardware.raspberryPi.RaspberryPiHealth
import com.v6ak.hagen.modules.ConfigModule
import com.v6ak.hagen.output.*

import java.nio.file.{Files, Paths}

object ExampleMain:

  def main(args: Array[String]): Unit = {
    val modules: Seq[HagenModule] = Seq(
      // Don't care about the order of the modules too much. They are scheduled, so that each module has satisfied its
      // dependencies, regardless of the order you specify it.

      // warn when Raspi power supply is bad
      RaspberryPiHealth(rpiPowerStatus),
      // configure breaking changes
      ConfigModule(BreakingChangesConfig()),
      // Fridge in defined in Fridge.scala
      Fridge,
      FridgeDashboard(),

      // TODO: Dehumidifier
      // TODO: Lights
      // TODO: other config

      // create dashboards
      HighlightableNotifications(autoClear = true),
      HighlightableDashboard(),

      // ==== put things to the configurations.yaml ====

      // Alternatively, you can skip this and put them in a separate file (see below). In ExampleMain, we produce both.
      // There is usually no reason to produce separate files with content that is already in configuration.yaml.
      // We do it just for demonstration that both is possible.
      AutomationsTopLevel(),
      TemplatesTopLevel,
      UtilityMetersTopLevel,

      // You can try to remove the CountersTopLevel. It should produce an error message like this:
      // java.lang.RuntimeException: Some items are used, but aren't defined: HashSet(counter.fridge_door_open_counter)
      // Because (1) we define some counter, (2) we use the defined counter and (3) we don't produce an extra file for
      // counters, Hagen detects that there is something missing.
      // Note that this doesn't cover all errors. It probably will not save you if you forgot automations (as you
      // probably don't reference them).
      CountersTopLevel,

      // You can try to remove InputBooleansTopLevel. Hagen will not throw any error, as it configured to also produce
      // hagen-input-booleans.yaml (see below). Hagen assumes you use the hagen-input-booleans.yaml file.
      InputBooleansTopLevel,

    )
    val evaluationResult = ModuleEvaluation.evalDependentModules(modules)
    val res = evaluationResult.params

    // We can get list of keys that weren't used during the evaluation. This can potentially indicate some issue
    val unused = res.keySet -- evaluationResult.keysUsed
    println(s"Unused keys: $unused")

    // After processing, we want to make some output files. In an extreme case, you might want to create a single
    // configuration.yaml file. However, you can also generate:
    // a. A file to include in configuration.yaml
    // b. A dashboard file
    // c. A file you can include in Jinja2 template within Ansible
    // At this moment, it is more complex that it should be.
    val outDir = Paths.get("out")
    Files.createDirectories(outDir)
    output(
      name => outDir.resolve(f"hagen-$name.yaml"),
      // When you use an entity that is neither defined in DefinedItems nor defined by you, you get a failure.
      DefinedItems,
      Map(
        // the toplevel file which is to be included in configuration.yaml
        "toplevel" -> YamlOut(
          res(TopLevelKeys).toSeq.map(pair => MapElement(Map(pair)))
        ),

        // It is already included in toplevel (due to AutomationsTopLevel), but you can also have an extra file.
        // Usually, there is no reason to have both, but we can do so.
        "automations" -> YamlOut(res(Automations)),

        // This is also possible for sensors. We generate a file indented by two spaces (indent = " " * 2 * 1).
        // This is useful when you use Ansible and include the file in configuration.yaml
        "sensors" -> YamlOut(Seq(SeqElement(res(Sensors))), indent = " " * 2 * 1),

        "input-booleans" -> YamlOut(Seq(MapTuplesElement(res(InputBooleans))), indent = " " * 2 * 1),
        "dashboard-overview" -> YamlOut(
          Seq(SeqElement(Seq(RawElement(res(DashboardParts), variables = Set(), defined = Set())))), indent = "  "),
      ) ++ res(DashboardPages).map((name, page) =>
        s"dashboard-page-$name" -> YamlOut(Seq(SeqElement(Seq(page))), indent = " " * 2 * 1)
      )
    )

  }


