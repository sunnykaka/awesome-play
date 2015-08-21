package common.utils.play;

import common.utils.Money;
import common.utils.play.interceptor.ActionFilter;
import common.utils.play.interceptor.ActionFilterChain;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.data.format.Formatters;
import play.inject.Injector;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Created by liubin on 15-4-21.
 */
public class BaseGlobal extends GlobalSettings {

    //Guice 容器
    public static Injector injector;
    //spring 容器
    public static ApplicationContext ctx;

    public static boolean isDev() {
        return "dev".equalsIgnoreCase(Play.application().configuration().getString("project.env"));
    }

    public static boolean isProd() {
        return "prod".equalsIgnoreCase(Play.application().configuration().getString("project.env"));
    }

    public static boolean isTest() {
        return "test".equalsIgnoreCase(Play.application().configuration().getString("project.env"));
    }

    protected Action createActionWithActionFilters(ActionFilter... actionFilters) {

        return new Action.Simple() {
            public F.Promise<Result> call(Http.Context ctx) throws Throwable {

                ActionFilterChain filterChain = new ActionFilterChain(
                        ctx,
                        delegate,
                        actionFilters
                );
                filterChain.doFilter();
                return filterChain.result;

            }
        };
    }

    protected boolean isJsonResponse(Http.RequestHeader request) {
        return request.acceptedTypes().stream().filter(mr -> mr.toString().contains("json")).findFirst().isPresent();
    }

    protected F.Promise<Result> showResult(Result result, boolean jsonResponse) {
        if (!jsonResponse && isDev()) {
            //开发环境显示play的错误页面
            return null;
        } else {
            return F.Promise.pure(result);
        }
    }

    protected synchronized void start(Application app, Class<?>... classes) {
        super.onStart(app);
        injector = app.injector();
        ctx = new AnnotationConfigApplicationContext(classes);

        Formatters.register(DateTime.class, new JodaDateFormatter());
        Formatters.register(Money.class, new MoneyFormatter());

        runSchedulers();
    }

    protected synchronized void stop(Application app) {
        stopSchedulers();

        injector = null;
        if (ctx != null) {
            ((AnnotationConfigApplicationContext) ctx).close();
            ctx = null;
        }
        super.onStop(app);
    }

    protected void runSchedulers() {}

    protected void stopSchedulers() {}
}
