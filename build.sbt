// See README.md for license details.

ThisBuild / scalaVersion := "2.13.8"

val chiselVersion = "3.5.1"

lazy val root = (project in file("."))
  .settings(
    name := "fpga-tidbits",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % chiselVersion,
      "edu.berkeley.cs" %% "chiseltest" % "0.5.1" % "test",
      "edu.berkeley.cs" %% "chisel-iotesters" % "2.5.4"
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-P:chiselplugin:genBundleElements",
      "-language:postfixOps"
    ),
    addCompilerPlugin(
      "edu.berkeley.cs" % "chisel3-plugin" % chiselVersion cross CrossVersion.full
    )
  )
