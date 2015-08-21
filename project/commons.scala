import sbt._
import Keys._

object Commons {
  val appVersion = "1.0.0-SNAPSHOT"
  val commonScalaVersion = "2.11.6"

  val settings: Seq[Def.Setting[_]] = Seq(
    version := appVersion,
    scalaVersion := commonScalaVersion,
    scalacOptions := Seq("-encoding", "UTF-8", "-Xlint", "-deprecation", "-unchecked", "-feature"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8", "-Xlint:-options"),
    doc in Compile <<= target.map(_ / "none"),
    resolvers ++= Seq(
      Opts.resolver.mavenLocalFile,
      //"local repository" at "http://192.168.1.100:8081/nexus/content/groups/public/",
      Resolver.typesafeRepo("releases"),
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots"),
      "pk11 repo" at "http://pk11-scratch.googlecode.com/svn/trunk",
      "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
    )
    /*
    credentials += Credentials("Sonatype Nexus Repository Manager",
      "192.168.1.100", "admin", "admin123"),
    publishTo <<= version { v: String =>
      val nexus = "http://192.168.1.100:8081/nexus/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "content/repositories/releases")
    }
    */
  )
}
