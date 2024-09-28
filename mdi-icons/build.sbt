ThisBuild / version := "0.1.0-SNAPSHOT"

//ThisBuild / scalaVersion := "3.2.0"

lazy val iconsStub = (project in file("icons-stub"))

lazy val mdiIcons = (project in file("."))
  .settings(
    name := "mdi-icons",
    crossPaths := false,
    autoScalaLibrary := false,
  )
  .aggregate(iconsStub)
  .dependsOn(iconsStub % "provided") // stubs should not be included in the final project
