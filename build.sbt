lazy val buildVersion = "1.0.1"

lazy val commonSettings = Seq(
  organization := "com.planetholt",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  version := buildVersion,
  scalaVersion := "2.11.8",
  startYear := Option(2016)
)

lazy val rootBuildSettings = Seq(
  name := "itunes-metadata",
  description := "A tool to retrieve TV Show metadata from the iTunes Affiliate API",
  homepage := Some(url("https://github.com/bpholt/video-encoding")),
  libraryDependencies ++= {
    val specs2Version = "3.7.2"
    val json4sVersion = "3.4.2"
    Seq(
      "com.github.scopt" %% "scopt" % "3.5.0",
      "org.skinny-framework" %% "skinny-http-client" % "2.1.2",
      "org.json4s" %% "json4s-native" % json4sVersion,
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.specs2" %% "specs2-core" % specs2Version % Test,
      "org.specs2" %% "specs2-mock" % specs2Version % Test
    )
  },
  resolvers += Resolver.sonatypeRepo("public"),
  buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
  buildInfoPackage := "com.planetholt.itunes.generated"
)

lazy val macroBuildSettings = Seq(
  name := "video-metadata-macros",
  homepage := Some(url("https://gist.github.com/mbedward/6e3dbb232bafec0792ba")),
  description := "macros to convert case classes to Maps and back",
  libraryDependencies ++= {
    Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  }
)

lazy val bintraySettings = Seq(
  //  bintrayVcsUrl := Some("https://github.com/bpholt/video-encoding"),
  //  publishMavenStyle := false,
  //  bintrayRepository := "maven",
  //  pomIncludeRepository := { _ ⇒ false }
)

lazy val macros = (project in file("macros"))
  .settings(commonSettings ++ macroBuildSettings)

lazy val app = (project in file("."))
  .settings(commonSettings ++ rootBuildSettings ++ bintraySettings: _*)
  .dependsOn(macros)
  .enablePlugins(BuildInfoPlugin)

lazy val pipeline = TaskKey[Unit]("pipeline", "CI Pipeline")
pipeline <<= test in Test
