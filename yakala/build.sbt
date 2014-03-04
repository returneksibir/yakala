name := "yakala"

version := "0.1"

scalaVersion := "2.10.3"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.10.3"

libraryDependencies += "org.scala-lang" % "scala-actors" % "2.10.3"

libraryDependencies += "org.scala-lang" % "scala-library" % "2.10.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "latest.release"

libraryDependencies += "org.jsoup" % "jsoup" % "latest.release"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

// libraryDependencies ++= Seq(
//   "org.scalesxml" %% "scales-xml" % "0.5.0-M1" ,
//   "org.scalesxml" %% "scales-jaxen" % "0.5.0-M1" intransitive(),
//   "jaxen" % "jaxen" % "1.1.3" intransitive(),
//   "org.scalesxml" %% "scales-aalto" % "0.5.0-M1"
// )

// libraryDependencies += "nu.validator.htmlparser" % "htmlparser" % "latest.release"

libraryDependencies += "jaxen" % "jaxen" % "latest.release"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies += "io.spray" % "spray-client" % "1.2.0"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11.0-M3" % "2.2.0"
