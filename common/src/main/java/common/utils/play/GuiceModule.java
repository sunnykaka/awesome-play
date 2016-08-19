package common.utils.play;

import common.utils.Money;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import play.Application;
import play.data.format.Formatters;
import play.inject.ApplicationLifecycle;
import play.inject.Injector;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

/**
 * Created by liubin on 15-4-21.
 */
public class GuiceModule {

    private Formatters formatters;

    public static Injector injector;

    @Inject
    public GuiceModule(Application application, Formatters formatters) {
        this.formatters = formatters;
        injector = application.injector();

        start();
    }

    private void start() {

        formatters.register(DateTime.class, new JodaDateFormatter());
        formatters.register(Money.class, new MoneyFormatter());

    }

}
