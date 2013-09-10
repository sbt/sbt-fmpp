import sbt._
import Keys.libraryDependencies

import fmpp.FmppPlugin._

object build extends Build {
  lazy val root = Project("main", file("."), settings = Defaults.defaultSettings ++ fmppSettings).configs(Fmpp)
}
