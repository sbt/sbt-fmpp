sbtPlugin := true

name := "sbt-fmpp"

version := "0.3-SNAPSHOT"

organization := "com.github.sbt"

scalaVersion        := "2.10.0"

crossScalaVersions  := Seq("2.9.0-1", "2.9.1-1", "2.9.2", "2.10.0")

ScriptedPlugin.scriptedSettings

ScriptedPlugin.scriptedBufferLog := false
