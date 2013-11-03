import sbt._
import Keys._

object YakalaBuild extends Build {
  lazy val yakala = project
  lazy val yakala_admin = project.dependsOn(yakala)
}
