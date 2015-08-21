package common.utils.play.interceptor;

/**
 * Created by liubin on 15/4/12.
 */
public interface ActionInterceptor {

    void preHandle() throws Exception;

    void postHandle() throws Exception;

    void afterCompletion(Exception exception) throws Exception;

}
