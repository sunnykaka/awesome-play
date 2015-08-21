package common.play.inject

import javax.inject.Inject

import common.utils.play.BaseGlobal
import play.api.inject.BindingKey
import play.api.inject.guice.{GuiceKey, GuiceInjector}
import play.mvc.Controller

import scala.reflect.ClassTag

/**
 * 这个Injector在查找类的时候，判断如果是play.mvc.Controller的子类的话，
 * 先从spring容器中查找类
 *
 * Created by liubin on 15-7-3.
 */
class SkInjector @Inject() (injector: com.google.inject.Injector) extends GuiceInjector(injector) {

  /**
   * Get an instance of the given class from the injector.
   */
  override def instanceOf[T](implicit ct: ClassTag[T]) = {
    instanceOf(ct.runtimeClass.asInstanceOf[Class[T]])
  }

  /**
   * Get an instance of the given class from the injector.
   */
  override def instanceOf[T](clazz: Class[T]) = {
    var bean: Option[T] = None
    if(classOf[Controller].isAssignableFrom(clazz)) {
      bean = Option(BaseGlobal.ctx.getBean(clazz))
    }
    if(bean.isEmpty) {
      bean = Option(super.instanceOf(clazz))
    }
    bean.fold(null.asInstanceOf[T])(x => x)
  }

  /**
   * Get an instance bound to the given binding key.
   */
  override def instanceOf[T](key: BindingKey[T]) = {
    var bean: Option[T] = None
    if(classOf[Controller].isAssignableFrom(key.clazz)) {
      bean = Option(BaseGlobal.ctx.getBean(key.clazz))
    }
    if(bean.isEmpty) {
      bean = Option(super.instanceOf(key))
    }
    bean.fold(null.asInstanceOf[T])(x => x)
  }


}
