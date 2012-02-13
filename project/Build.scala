import sbt._
import Keys._

object FmppBuild extends Build {
  lazy val project = Project(
    id = "root", 
    base = file("."),
    settings = Defaults.defaultSettings ++ Publishing.settings
  )
}
