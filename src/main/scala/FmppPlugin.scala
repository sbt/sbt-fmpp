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
  val fmppSources =  SettingKey[Seq[String]]("fmpp-sources", "Sources type to be processed.")
  val fmppVersion =  SettingKey[String]("fmpp-version", "FMPP version.")

  lazy val fmppSettings = Seq[Project.Setting[_]](
    fmppArgs := Seq("--ignore-temporary-files"),
    fmppMain := "fmpp.tools.CommandLine",
    fmppSources := Seq("scala", "java"),
    fmppVersion := "0.9.14",
    libraryDependencies <+= (fmppVersion in Fmpp)("net.sourceforge.fmpp" % "fmpp" % _ % Fmpp.name),
    sourceDirectory in Fmpp <<= (sourceDirectory in Compile),
    scalaSource in Fmpp <<= (sourceManaged in Compile),

    managedClasspath in Fmpp <<= (classpathTypes, update) map { (ct, report) =>
      Classpaths.managedJars(Fmpp, ct, report)
    },

    fmpp <<= (
      fmppSources in Fmpp,
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
    sources: Seq[String],
    source: File,
    sourceManaged: File,
    mainClass: String,
    args: Seq[String],
    classpath: Classpath,
    javaHome: Option[File],
    streams: TaskStreams
  ) = {
    sources.flatMap(x => {
      val input = new File(source, x)
      if (input.exists) {
        val output = new File(sourceManaged, x)
        IO.delete(output)
        Fork.java(
          javaHome,
          List(
            "-cp", classpath.map(_.data).mkString(":"), mainClass,
            "-S", input.toString, "-O", output.toString,
             "--replace-extensions=fm, " + x
          ) ::: args.toList,
          streams.log
        )
        (output ** ("*." + x)).get
      } else Nil
    })
  }
}
