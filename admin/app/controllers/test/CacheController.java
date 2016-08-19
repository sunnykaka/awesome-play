package controllers.test;


import common.utils.play.BaseGlobal;
import play.Logger;
import play.cache.CacheApi;
import play.mvc.Controller;
import play.mvc.Result;

@org.springframework.stereotype.Controller
public class CacheController extends Controller {

    public Result getCache(String key) {

        try {
            String value = cacheApi().getOrElse(key, () -> "set default value", 0);
            return ok(value);
        } catch (Exception e) {
            Logger.error("", e);
            return internalServerError(e.getMessage());
        }
    }

    public Result setCache(String key, String value) {
        cacheApi().set(key, value);

        return ok(String.valueOf(cacheApi().get(key)));
    }

    protected CacheApi cacheApi() {
        return BaseGlobal.injector.instanceOf(CacheApi.class);
    }


}