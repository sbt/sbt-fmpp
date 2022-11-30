# sbt-fmpp

[SBT](http://github.com/harrah/xsbt) plugin to generate scala/java code using [FreeMarker](http://freemarker.sourceforge.net/) template.

Processing is done using [FMPP](http://fmpp.sourceforge.net/).

## Usage

Adding the plugin:

    addSbtPlugin("com.github.sbt" %% "sbt-fmpp" % "0.3")

Enable the plugin in your build:

    import fmpp.FmppPlugin._

    object build extends Build {
      lazy val root = Project("main", file("."), settings = Defaults.defaultSettings ++ fmppSettings).configs(Fmpp)
    }

Once activated place your '.fm' templates in *src/main/scala* or *src/main/java* and that's all!

## Contribution Policy

Contributions via GitHub pull requests are gladly accepted from their original author.
Along with any pull requests, please state that the contribution is your original work and 
that you license the work to the project under the project's open source license.
Whether or not you state this explicitly, by submitting any copyrighted material via pull request, 
email, or other means you agree to license the material under the project's open source license and 
warrant that you have the legal authority to do so.
