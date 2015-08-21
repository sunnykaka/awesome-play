package common.play.kamon

import kamon.Kamon.tracer
import kamon.util.SameThreadExecutionContext
import play.api.mvc._
import play.api.mvc.Results._

/**
 * Created by liubin on 15-7-14.
 */
class KamonRequestFilter extends EssentialFilter {

  override def apply(next: EssentialAction): EssentialAction = {
    val action = (requestHeader: RequestHeader) ⇒ {
      val playExtension = kamon.Kamon(Play)

      val token = if (playExtension.includeTraceToken) {
        requestHeader.headers.get(playExtension.traceTokenHeaderName)
      } else None

      val context = tracer.newContext(playExtension.generateTraceName(requestHeader), token)
//      play.api.Logger.warn(s"before KamonRequestFilter... uri: ${requestHeader.uri}, thread: ${Thread.currentThread()}, current context: $context}")

      next(requestHeader).map { result =>
//          play.api.Logger.debug(s"ok after KamonRequestFilter... uri: ${requestHeader.uri}, thread: ${Thread.currentThread()}, current context: $ctx")
        context.finish()
        playExtension.httpServerMetrics.recordResponse(context.name, result.header.status.toString)

        if (playExtension.includeTraceToken) result.withHeaders(playExtension.traceTokenHeaderName -> context.token)
        else result

      }(SameThreadExecutionContext).recover {case t: Throwable=>
        //exception thrown in action
//          play.api.Logger.debug(s"error after KamonRequestFilter... uri: ${requestHeader.uri}, thread: ${Thread.currentThread()}, current context: $ctx")
        context.finish()
        playExtension.httpServerMetrics.recordResponse(context.name, InternalServerError.header.status.toString)

        //throw to caller
        throw t

      }(SameThreadExecutionContext)
    }

    EssentialAction(action)
  }
}
