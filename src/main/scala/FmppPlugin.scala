package fmpp

import sbt._
import Keys._
import sbt.Fork

import java.io.File

object FmppPlugin extends Plugin {
  val Fmpp = config("fmpp")

  val fmpp = TaskKey[Seq[File]]("fmpp", "Generate Scala sources from FMPP Scala Template")
  val fmppArgs = SettingKey[Seq[String]]("fmpp-args", "Extra command line parameters to FMPP.")
  val fmppMain = SettingKey[String]("fmpp-main", "FMPP main class.")
  val fmppVersion =  SettingKey[String]("fmpp-version", "FMPP version.")

  lazy val fmppSettings = Seq[Project.Setting[_]](
    fmppArgs := Seq("--ignore-temporary-files"),
    fmppMain := "fmpp.tools.CommandLine",
    fmppVersion := "0.9.14",
    libraryDependencies <+= (fmppVersion in Fmpp)("net.sourceforge.fmpp" % "fmpp" % _ % Fmpp.name),
    sourceDirectory in Fmpp <<= (sourceDirectory in Compile) { _ / "scala-template" },
    scalaSource in Fmpp <<= (sourceManaged in Compile),

    managedClasspath in Fmpp <<= (classpathTypes, update) map { (ct, report) =>
      Classpaths.managedJars(Fmpp, ct, report)
    },

    fmpp <<= (
      sourceDirectory in Fmpp,
      sourceManaged in Fmpp,
      fmppMain in Fmpp,
      fmppArgs in Fmpp,
      managedClasspath in Fmpp,
      javaHome,
      streams
    ).map(process),

    sourceGenerators in Compile <+= (fmpp).task
  )

  private def process(
    source: File,
    sourceManaged: File,
    mainClass: String,
    args: Seq[String],
    classpath: Classpath,
    javaHome: Option[File],
    streams: TaskStreams
  ) = {
    Fork.java(
      javaHome,
      List(
        "-cp", classpath.map(_.data).mkString(":"), mainClass,
        "-S", source.toString, "-O", sourceManaged.toString
      ) ::: args.toList,
      streams.log
    )
    (sourceManaged ** "*.scala").get
  }
}
