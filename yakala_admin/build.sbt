import com.typesafe.sbt.SbtStartScript
import SbtStartScript.StartScriptKeys.startScriptName

name := "yakala-admin"

version := "0.1"

scalaVersion := "2.10.3"

seq(SbtStartScript.startScriptForClassesSettings: _*)

startScriptName <<= target / "yakala-admin"
