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

  override lazy val projectSettings = Seq(
    ivyConfigurations += Fmpp,
    libraryDependencies += "net.sourceforge.fmpp" % "fmpp" % (Fmpp / fmppVersion).value % Fmpp.name,

  ) ++ inConfig(Fmpp)(fmppSettings)

  lazy val fmppSettings = Seq[Setting[_]](
    fmppArgs := Seq("--ignore-temporary-files"),
    fmppMain := "fmpp.tools.CommandLine",
    fmppSources := Seq("scala", "java"),
    fmppVersion := "0.9.16",
    sourceDirectory := (Compile / sourceDirectory).value,
    scalaSource := (Compile / sourceManaged).value / "fmpp",
    managedClasspath := Classpaths.managedJars(Fmpp, classpathTypes.value, update.value),
    fmpp := {
      process(
        fmppSources.value,
        sourceDirectory.value,
        scalaSource.value,
        fmppMain.value,
        fmppArgs.value,
        managedClasspath.value,
        javaHome.value,
        streams.value,
        streams.value.cacheDirectory
      )
    },

    Compile / sourceGenerators += fmpp
  )

  private def process(
    sources: Seq[String],
    sourceDir: File,
    output: File,
    mainClass: String,
    args: Seq[String],
    classpath: Classpath,
    javaHome: Option[File],
    streams: TaskStreams,
    cache: File
  ) = {

    sources.flatMap{ source =>
      val input = sourceDir / source
      if (input.exists) {
        val cached = FileFunction.cached(cache / "fmpp" / source, FilesInfo.lastModified, FilesInfo.exists) {
          _ => {
            IO.delete(output / source)
            val options = List(
              "-cp", classpath.map(_.data).mkString(File.pathSeparator), 
              mainClass,
              "-S", input.toString,
              "-O", output.toString,
              "--replace-extensions=fm, " + source,
              "-M", "execute(**/*.fm), ignore(**/*)"
            ) ::: args.toList


            streams.log.info("args: ")
            options.foreach(option => streams.log.info(option))
            

            Fork.java(
              ForkOptions().withJavaHome(javaHome),
              options
            )
            (output ** ("*." + source)).get.toSet
          }
        }
        cached((input ** "*.fm").get.toSet)
      } else Nil
    }
  }
  
}
