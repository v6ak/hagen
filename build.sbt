ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

lazy val hagen = (project in file("."))
  .settings(
    name := "hagen"
  )


libraryDependencies += "org.snakeyaml" % "snakeyaml-engine" % "2.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.13" % "test"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0"

scalacOptions := Seq("-unchecked", "-deprecation")