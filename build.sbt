sbtPlugin := true

name := "sbt-fmpp"

version := "0.3-SNAPSHOT"

organization := "com.github.sbt"


crossScalaVersions  := Seq("2.12.6", "2.11.11")

scalaVersion := crossScalaVersions.value.head

sbtPlugin := true

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
publishTo := {
  if (isSnapshot.value)
    Some(Resolver.sbtPluginRepo("snapshots"))
  else
    Some(Resolver.sbtPluginRepo("releases"))
}
publishMavenStyle := false
