import AssemblyKeys._

assemblySettings

jarName in assembly := "yakala.jar"

mainClass in assembly := Some("yakala.console.Console")
