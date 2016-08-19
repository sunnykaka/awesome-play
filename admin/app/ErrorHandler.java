import common.utils.play.AbstractErrorHandler;
import play.Configuration;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.twirl.api.Content;
import views.html.error_400;
import views.html.error_404;
import views.html.error_500;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Created by liubin on 2016/8/12.
 */
@Singleton
public class ErrorHandler extends AbstractErrorHandler {

    @Inject
    public ErrorHandler(Configuration configuration,
                        Environment environment,
                        OptionalSourceMapper sourceMapper,
                        Provider<Router> routes) {
        super(configuration, environment, sourceMapper, routes);
    }

    @Override
    protected Content error500Page(String errorMessage) {
        return error_500.render();
    }

    @Override
    protected Content error404Page(String errorMessage, String requestMethod, String requestUri) {
        return error_404.render();
    }

    @Override
    protected Content error400Page(String errorMessage, String requestMethod, String requestUri) {
        return error_400.render();
    }
}
