import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.web.Import._
import sbt._
import Keys._
import Dependencies._

object ApplicationBuild extends Build {

  lazy val common = (project in file("common")).
    settings(Commons.settings: _*).
    settings(
      libraryDependencies ++= commonDependencies,
      unmanagedBase := baseDirectory.value / "lib"
    )

  lazy val order = (project in file("order-center")).
    settings(Commons.settings: _*).
    settings(
      libraryDependencies ++= orderDependencies
    ).
    dependsOn(common)

  lazy val admin = webProject("admin", adminDependencies)

  def webProject(name: String, dependencies: Seq[ModuleID]) = {
    Project(name, file(name)).
    enablePlugins(play.sbt.PlayJava).
    settings(Commons.settings: _*).
    settings(
      libraryDependencies ++= dependencies,
      sourceGenerators in Compile += task {
        val dir: File = (sourceManaged in Compile).value / "controllers"
        val dirs = Seq(dir / "ref", dir / "javascript")
        dirs.foreach(_.mkdirs)
        Seq[File]()
      },
      unmanagedSourceDirectories in Compile += (sourceManaged in Compile).value,
      pipelineStages := Seq(digest, gzip)
    ).dependsOn(common % "test->test;compile->compile").
    dependsOn(order)
  }


  lazy val root = (project in file(".")).
    settings(Commons.settings: _*).
    aggregate(admin)
}
