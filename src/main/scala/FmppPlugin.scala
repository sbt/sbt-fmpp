package fmpp

import sbt._
import Keys._
import sbt.Fork

import java.io.File

object FmppPlugin extends AutoPlugin {
  object autoImport {
    lazy val Fmpp = config("fmpp").hide
    lazy val fmpp = TaskKey[Seq[File]]("fmpp", "Generate Scala sources from FMPP Scala Template")
    lazy val fmppArgs = SettingKey[Seq[String]]("fmpp-args", "Extra command line parameters to FMPP.")
    lazy val fmppMain = SettingKey[String]("fmpp-main", "FMPP main class.")
    lazy val fmppSources =  SettingKey[Seq[String]]("fmpp-sources", "Sources type to be processed.")
    lazy val fmppVersion =  SettingKey[String]("fmpp-version", "FMPP version.")
  }

  import autoImport._

  // TODO Add support for Compile/Test/...
  // https://github.com/sbt/sbt-xjc/blob/master/src/main/scala/com/github/retronym/sbtxjc/SbtXjcPlugin.scala
  //def fmppSettings0(config: Config) = Seq(sourceDirectory in fmpp in config := … , …) `
  //val fmppSettings = fmppSettings0(Compile)

  lazy val fmppSettings = Seq[Setting[_]](
    fmppArgs := Seq("--ignore-temporary-files"),
    fmppMain := "fmpp.tools.CommandLine",
    fmppSources := Seq("scala", "java"),
    fmppVersion := "0.9.14",
    libraryDependencies += ("net.sourceforge.fmpp" % "fmpp" % (fmppVersion in Fmpp).value % Fmpp.name),
    sourceDirectory in Fmpp := (sourceDirectory in Compile).value,
    scalaSource in Fmpp := (sourceManaged in Compile).value,

    managedClasspath in Fmpp := Classpaths.managedJars(Fmpp, classpathTypes.value, update.value),

    fmpp := {
      process(
        (fmppSources in Fmpp).value,
        (sourceDirectory in Fmpp).value,
        (sourceManaged in Fmpp).value,
        (fmppMain in Fmpp).value,
        (fmppArgs in Fmpp).value,
        (managedClasspath in Fmpp).value,
        javaHome.value,
        streams.value,
        streams.value.cacheDirectory
      )
    },

    sourceGenerators in Compile += fmpp
  )

  private def process(
    sources: Seq[String],
    source: File,
    sourceManaged: File,
    mainClass: String,
    args: Seq[String],
    classpath: Classpath,
    javaHome: Option[File],
    streams: TaskStreams,
    cache: File
  ) = {
    sources.flatMap(x => {
      val input = source / x
      if (input.exists) {
        val output = sourceManaged / x
        val cached = FileFunction.cached(cache / "fmpp" / x, FilesInfo.lastModified, FilesInfo.exists) {
          (in: Set[File]) => {
            IO.delete(output)
            val options = List(
              "-cp", classpath.map(_.data).mkString(File.pathSeparator), mainClass,
              "-S", input.toString, "-O", output.toString,
              "--replace-extensions=fm, " + x,
              "-M", "execute(**/*.fm), ignore(**/*)"
            ) ::: args.toList
            println(options)
            Fork.java(
              ForkOptions().withJavaHome(javaHome),
              options
            )
            (output ** ("*." + x)).get.toSet
          }
        }
        cached((input ** "*.fm").get.toSet)
      } else Nil
    })
  }
  override lazy val projectSettings = inConfig(Fmpp)(fmppSettings)
}
