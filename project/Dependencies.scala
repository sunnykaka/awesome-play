import play.sbt.PlayImport._
import sbt._
import Keys._

object Dependencies {

  val mysqlConnectorVersion = "5.1.32"
  val springVersion = "4.2.6.RELEASE"

  val kamonVersion = "0.4.0"

  val springHibernate = Seq(
    "org.springframework" % "spring-context" % springVersion,
    "org.springframework" % "spring-orm" % springVersion,
    "org.springframework" % "spring-jdbc" % springVersion,
    "org.springframework" % "spring-tx" % springVersion,
    "org.springframework" % "spring-expression" % springVersion,
    "org.springframework" % "spring-aop" % springVersion,
    "org.springframework" % "spring-test" % springVersion % "test" exclude("junit", "junit"),
    "org.hibernate" % "hibernate-entitymanager" % "4.3.6.Final"
  )

  val common = Seq(
    "junit" % "junit" % "4.11" % "test" exclude("org.hamcrest", "hamcrest-core"),
    "org.jadira.usertype" % "usertype.core" % "3.2.0.GA" exclude("junit", "junit"),
    "com.fasterxml.jackson.datatype" % "jackson-datatype-hibernate4" % "2.5.4",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.5.4",
    "com.typesafe.play" %% "play-mailer" % "3.0.1"
  )

  val playDependencies = Seq(
    javaCore,
    javaWs,
    cache,
    javaJdbc,
    "com.typesafe.play.modules" %% "play-modules-redis" % "2.4.1" exclude("junit", "junit"),
    "io.kamon" %% "kamon-core" % kamonVersion,
    "io.kamon" %% "kamon-statsd" % kamonVersion,
    "io.kamon" %% "kamon-system-metrics" % kamonVersion exclude("io.kamon", "sigar-loader"),
    "io.kamon" %  "sigar-loader" % "1.6.6"
  )

  val commonDependencies: Seq[ModuleID] = common ++ springHibernate ++ playDependencies

  val orderDependencies: Seq[ModuleID] = Seq("jdom" % "jdom" % "1.0")

  val adminDependencies: Seq[ModuleID] = Seq("mysql" % "mysql-connector-java" % mysqlConnectorVersion)


}