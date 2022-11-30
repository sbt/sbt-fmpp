ThisBuild / scalaVersion := "2.12.16"

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-fmpp",
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    scriptedBufferLog := false,
    // sbt-fmpp requires sbt 1.5.0 and up
    pluginCrossBuild / sbtVersion := "1.5.0",
  )

ThisBuild / description := "sbt plugin to generate scala/java code using FreeMarker template. Processing is done using FMPP."
ThisBuild / organization := "com.github.sbt"
ThisBuild / homepage := Some(url("https://github.com/sbt/sbt-fmpp"))
ThisBuild / Compile / scalacOptions := Seq("-feature", "-deprecation", "-Xlint")
ThisBuild / licenses := List("Apache License v2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))
ThisBuild / developers := List(
  Developer(
    "aloiscochard",
    "Aloïs Cochard",
    "@aloiscochard",
    url("https://github.com/aloiscochard")
  ),
  Developer(
    "MasseGuillaume",
    "Guillaume Massé",
    "@MasseGuillaume",
    url("https://github.com/MasseGuillaume")
  )
)
ThisBuild / pomIncludeRepository := { _ =>
  false
}
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
ThisBuild / dynverSonatypeSnapshots := true