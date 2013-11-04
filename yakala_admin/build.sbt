import com.typesafe.sbt.SbtStartScript

name := "yakala-admin"

version := "0.1"

scalaVersion := "2.10.3"

seq(SbtStartScript.startScriptForClassesSettings: _*)
