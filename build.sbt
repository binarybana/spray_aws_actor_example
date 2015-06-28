name := "spray-sample"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.3"

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation"
  ,"-unchecked"
  ,"-encoding", "UTF-8"
  ,"-target:jvm-1.7"
  // "-optimise"   // this option will slow your build
)

scalacOptions ++= Seq(
  "-Yclosure-elim",
  "-Yinline"
)

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

val akkaVersion = "2.3.11"
val sprayVersion = "1.3.3"

/* dependencies */
libraryDependencies ++= Seq (
  // ,"com.amazonaws" % "aws-java-sdk" % "1.9.0"
  "com.github.seratch" %% "awscala" % "0.5.+"
  // -- testing --
  // -- Logging --
  ,"ch.qos.logback" % "logback-classic" % "1.1.2"
  // -- Akka --
  ,"com.typesafe.akka" %% "akka-actor" % akkaVersion
  ,"com.typesafe.akka" %% "akka-agent" % akkaVersion
  ,"com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  // -- Spray --
  ,"io.spray" %% "spray-routing" % sprayVersion
  ,"io.spray" %% "spray-can" % sprayVersion
  ,"io.spray" %% "spray-httpx" % sprayVersion
  // -- Json --
  ,"org.json4s" %% "json4s-native" % "3.2.11"
)

Revolver.settings
