name := """InventoryTracker"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "org.scalikejdbc" %% "scalikejdbc" % "2.5.2",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.5.2",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.5.1",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.9",
  ws
)

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _)
