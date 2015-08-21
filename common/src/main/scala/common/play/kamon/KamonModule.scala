package common.play.kamon

import javax.inject._
import kamon.metric.MetricsModule
import kamon.trace.TracerModule
import play.api.inject.{ ApplicationLifecycle, Module }
import play.api.{ Logger, Configuration, Environment }
import scala.concurrent.Future

trait Kamon {
  def start(): Unit
  def shutdown(): Unit
  def metrics(): MetricsModule
  def tracer(): TracerModule
}

class KamonModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = {
    Seq(bind[Kamon].to[KamonAPI].eagerly())
  }
}

@Singleton
class KamonAPI @Inject() (lifecycle: ApplicationLifecycle, environment: Environment) extends Kamon {

  private val log = Logger(classOf[KamonAPI])

  log.info("Registering the Kamon Play Module.")

  start() //force to start kamon eagerly on application startup

  def start(): Unit = kamon.Kamon.start()
  def shutdown(): Unit = kamon.Kamon.shutdown()
  def metrics(): MetricsModule = kamon.Kamon.metrics
  def tracer(): TracerModule = kamon.Kamon.tracer

  lifecycle.addStopHook { () ⇒
    Future.successful(shutdown())
  }
}
