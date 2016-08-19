package common.utils.play;

import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import common.utils.JsonResult;
import play.Configuration;
import play.Environment;
import play.Logger;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.http.HttpErrorHandlerExceptions;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;

import javax.inject.Provider;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by liubin on 2016/8/12.
 */
public abstract class AbstractErrorHandler extends DefaultHttpErrorHandler {

    private final Environment environment;

    private final OptionalSourceMapper sourceMapper;


    public AbstractErrorHandler(Configuration configuration, Environment environment,
                                OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(configuration, environment, sourceMapper, routes);
        this.environment = environment;
        this.sourceMapper = sourceMapper;
    }

    @Override
    public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {
        try {
            UsefulException usefulException = throwableToUsefulException(exception);
            Throwable cause = usefulException.cause;
            String errorMessage = "oops! 服务器开小差了, 请过会儿再来吧。";
            if(cause != null && (cause instanceof AppException || cause instanceof AppBusinessException)) {
                errorMessage = cause.getMessage();
            } else {
                logServerError(request, usefulException);
            }
            if(isJsonResponse(request)) {
                return CompletableFuture.completedFuture(
                        Results.internalServerError(new JsonResult(false, errorMessage).toNode()));
            } else {
                switch (environment.mode()) {
                    case PROD:
                        return CompletableFuture.completedFuture(
                                Results.internalServerError(error500Page(errorMessage)));
                    default:
                        return onDevServerError(request, usefulException);
                }
            }
        } catch (Exception e) {
            Logger.error("Error while handling error", e);
            return CompletableFuture.completedFuture(Results.internalServerError());
        }

    }


    @Override
    public CompletionStage<Result> onClientError(Http.RequestHeader request, int statusCode, String message) {
        if (statusCode == 400) {
            return onBadRequest(request, message);
        } else if (statusCode == 403) {
            return onForbidden(request, message);
        } else if (statusCode == 404) {
            return onNotFound(request, message);
        } else if (statusCode >= 400 && statusCode < 500) {
            return onOtherClientError(request, statusCode, message);
        } else {
            throw new IllegalArgumentException("onClientError invoked with non client error status code " + statusCode + ": " + message);
        }

    }

    @Override
    protected CompletionStage<Result> onNotFound(Http.RequestHeader request, String message) {
        String errorMessage = "您请求的页面没有找到，去其他地方逛逛吧";
        if(isJsonResponse(request)) {
            return CompletableFuture.completedFuture(
                    Results.notFound(new JsonResult(false, errorMessage).toNode()));
        } else {
            if (environment.isProd()) {
                return CompletableFuture.completedFuture(
                        Results.notFound(
                                error404Page(errorMessage, request.method(), request.uri())));
            } else {
                return super.onNotFound(request, message);
            }
        }
    }

    @Override
    protected CompletionStage<Result> onBadRequest(Http.RequestHeader request, String message) {
        String errorMessage = "您请求的参数有误";
        if(isJsonResponse(request)) {
            return CompletableFuture.completedFuture(
                    Results.badRequest(new JsonResult(false, errorMessage).toNode()));
        } else {
            if (environment.isProd()) {
                return CompletableFuture.completedFuture(
                        Results.badRequest(
                                error400Page(errorMessage, request.method(), request.uri())));
            } else {
                return super.onBadRequest(request, message);
            }
        }
    }

    @Override
    protected CompletionStage<Result> onForbidden(Http.RequestHeader request, String message) {
        String errorMessage = "无权访问";
        if(isJsonResponse(request)) {
            return CompletableFuture.completedFuture(
                    Results.forbidden(new JsonResult(false, errorMessage).toNode()));
        } else {
            return CompletableFuture.completedFuture(
                    Results.forbidden(error500Page(errorMessage)));
        }
    }

    @Override
    protected CompletionStage<Result> onOtherClientError(Http.RequestHeader request, int statusCode, String message) {
        return onBadRequest(request, message);
    }

    private boolean isJsonResponse(Http.RequestHeader request) {
        return request.acceptedTypes().stream().filter(mr -> mr.toString().contains("json")).findFirst().isPresent();
    }

    /**
     * Convert the given exception to an exception that Play can report more information about.
     *
     * This will generate an id for the exception, and in dev mode, will load the source code for the code that threw the
     * exception, making it possible to report on the location that the exception was thrown from.
     */
    private UsefulException throwableToUsefulException(final Throwable throwable) {
        return HttpErrorHandlerExceptions.throwableToUsefulException(sourceMapper.sourceMapper(), environment.isProd(), throwable);
    }

    protected abstract Content error500Page(String errorMessage);

    protected abstract Content error404Page(String errorMessage, String requestMethod, String requestUri);

    protected abstract Content error400Page(String errorMessage, String requestMethod, String requestUri);

}
