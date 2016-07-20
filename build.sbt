lazy val buildVersion = "0.0.1"

lazy val commonSettings = Seq(
  organization := "com.planetholt",
  homepage := Some(url("https://github.com/bpholt/video-encoding")),
  description := "",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  version := buildVersion,
  scalaVersion := "2.11.8",
  startYear := Option(2016)
)

lazy val rootBuildSettings = Seq(
  name := "video-metadata",
  libraryDependencies ++= {
    val specs2Version = "3.7.2"
    val json4sVersion = "3.4.0"
    Seq(
      "com.github.scopt" %% "scopt" % "3.5.0",
      "com.github.nscala-time" %% "nscala-time" % "2.12.0",
      "org.skinny-framework" %% "skinny-http-client" % "2.1.2",
      "org.json4s" %% "json4s-native" % json4sVersion,
      "org.json4s" %% "json4s-ext" % json4sVersion,
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.specs2" %% "specs2-core" % specs2Version % Test,
      "org.specs2" %% "specs2-mock" % specs2Version % Test
    )
  },
  resolvers += Resolver.sonatypeRepo("public")
)

lazy val macroBuildSettings = Seq(
  name := "video-metadata-macros",
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

lazy val pipeline = TaskKey[Unit]("pipeline", "CI Pipeline")
pipeline <<= test in Test
