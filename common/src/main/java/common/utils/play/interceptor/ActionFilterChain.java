package common.utils.play.interceptor;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liubin on 15/4/12.
 */
public class ActionFilterChain {

    public final ActionFilter[] filters;

    public int filterIndex = 0;

    public F.Promise<Result> result;

    public Http.Context ctx;

    public Action<?> delegate;

    public ActionFilterChain(Http.Context ctx, Action<?> delegate, ActionFilter... filters) {
        if(filters == null) {
            filters = new ActionFilter[0];
        }

        this.filters = filters;
        this.ctx = ctx;
        this.delegate = delegate;
    }

    public void doFilter() throws Throwable {
        if(filters.length <= filterIndex) {
            result = delegate.call(ctx);
        } else {
            filters[filterIndex++].doFilter(this);
        }
    }
}
