
name := """KepOpc"""

version := "0.0.1"

//lazy val root = (project in file(".")).enablePlugins(PlayJava)
lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)
scalaVersion := "2.11.7"
/*unmanagedJars in Compile += file("lib/sbteclipse-plugin.jar")*/
playEbeanModels in Compile := Seq("models.*")

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.eclipse.milo" % "milo" % "0.1.0",
  "org.eclipse.milo" % "sdk-client" % "0.2.4",
  "org.eclipse.milo" % "sdk-server" % "0.2.4",
  "org.eclipse.milo" % "stack-client" % "0.2.4",
  "org.eclipse.milo" % "stack-server" % "0.2.4",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.58",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
)
