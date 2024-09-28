ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val iconsStub = (project in file("."))
  .settings(
    name := "icons-stub",
    crossPaths := false,
    autoScalaLibrary := false,
  )
