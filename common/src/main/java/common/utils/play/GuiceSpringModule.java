package common.utils.play;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

/**
 * Created by liubin on 2016/8/12.
 */
public class GuiceSpringModule {

    private ApplicationContext ctx;

    @Inject
    public GuiceSpringModule(ApplicationLifecycle lifecycle, GuiceModule guiceModule) {
        ctx = initContext();
        lifecycle.addStopHook(() -> {
            if (ctx != null) {
                ((AnnotationConfigApplicationContext) ctx).close();
                ctx = null;
            }
            return CompletableFuture.completedFuture(null);
        });

    }

    private ApplicationContext initContext() {
        this.ctx = new AnnotationConfigApplicationContext("configs");
        return this.ctx;
    }

    public ApplicationContext getCtx() {
        return ctx;
    }

}
