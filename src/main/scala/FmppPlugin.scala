package fmpp

import sbt._
import Keys._

import java.io.File

object FmppPlugin extends Plugin {
  val Fmpp = config("xmpp")

  val fmpp = TaskKey[Seq[File]]("fmpp", "Generate Scala sources from FMPP Scala Template")
  val fmppArgs = SettingKey[Seq[String]]("fmpp-args", "Extra command line parameters to FMPP.")
  val fmppVersion =  SettingKey[String]("fmpp-version", "FMPP version.")

  lazy val fmppSettings = Seq[Project.Setting[_]](
    fmppArgs := Seq("--ignore-temporary-files"),
    fmppVersion := "0.9.14",
    libraryDependencies in Fmpp <+= (fmppVersion in Fmpp)("net.sourceforge.fmpp" % "fmpp" % _ % Fmpp.name),
    sourceDirectory in Fmpp <<= (sourceDirectory in Compile) { _ / "scala-fmpp" },
    scalaSource in Fmpp <<= (sourceManaged in Compile),

    managedClasspath in Fmpp <<= (classpathTypes, update) map { (ct, report) =>
      Classpaths.managedJars(Fmpp, ct, report)
    },
    sources in Fmpp <<= (sourceDirectory in Fmpp) map (identity(_) :: Nil),

    fmpp <<= (sources in Fmpp, sourceManaged in Fmpp, managedClasspath in Fmpp).map(process)
  )

  private def process(sources: Seq[File], sourceManaged: File, classpath: Classpath) = {
    println(sources)
    println(sourceManaged)
    println(classpath.map(_.data))
    (sourceManaged ** "*.scala").get
  }
}
