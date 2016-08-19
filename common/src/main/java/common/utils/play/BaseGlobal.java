package common.utils.play;

import org.springframework.context.ApplicationContext;
import play.Application;
import play.inject.ApplicationLifecycle;
import play.inject.Injector;

import java.util.concurrent.CompletableFuture;

/**
 * Created by liubin on 15-4-21.
 */
public abstract class BaseGlobal{

    //Guice 容器
    public static Injector injector;

    //spring 容器
    public static ApplicationContext ctx;

    //play application
    public static Application application;


    public BaseGlobal (Application application,
                       ApplicationLifecycle lifecycle,
                       GuiceModule guiceModule,
                       GuiceSpringModule guiceSpringModule) {
        BaseGlobal.application = application;
        BaseGlobal.injector = application.injector();
        BaseGlobal.ctx = guiceSpringModule.getCtx();

        start();

        lifecycle.addStopHook(() -> {
            stopSchedulers();
            return CompletableFuture.completedFuture(null);
        });
    }

    public static boolean isDev() {
        return "dev".equalsIgnoreCase(application.configuration().getString("project.env"));
    }

    public static boolean isProd() {
        return "prod".equalsIgnoreCase(application.configuration().getString("project.env"));
    }

    public static boolean isTest() {
        return "test".equalsIgnoreCase(application.configuration().getString("project.env"));
    }

    private void start() {

        runSchedulers();
    }

    protected void runSchedulers() {}

    protected void stopSchedulers() {}


    public Application getApplication() {
        return application;
    }

    public ApplicationContext getCtx() {
        return ctx;
    }

    public Injector getInjector() {
        return injector;
    }
}
