package common.play.inject

import play.api.ApplicationLoader.Context
import play.api.inject.guice.{GuiceableModule, GuiceApplicationLoader}

import play.api.inject.{bind, Injector}

/**
 * Created by liubin on 15-7-3.
 */
class SkGuiceApplicationLoader extends GuiceApplicationLoader {

  override protected def overrides(context: Context): Seq[GuiceableModule] = {
    super.overrides(context) ++ Seq[GuiceableModule](
      bind[Injector].to[SkInjector]
    )
  }
}
