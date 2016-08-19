package base;

import common.services.GeneralDao;
import common.utils.play.BaseGlobal;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.Application;
import play.Logger;
import play.api.ApplicationLoader;
import play.api.ApplicationLoader$;
import play.api.Environment$;
import play.api.Mode$;
import play.core.DefaultWebCommands;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import scala.Option;
import utils.Global;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.io.File;

/**
 * Created by liubin on 15/4/6.
 */
public abstract class BaseTest {

    protected static Application app;

    protected static Application provideApplication() {
        ApplicationLoader.Context context = ApplicationLoader$.MODULE$.createContext(
                Environment$.MODULE$.simple(new File("."), Mode$.MODULE$.Test()),
                new scala.collection.immutable.HashMap<>(),
                Option.empty(),
                new DefaultWebCommands());

        ApplicationLoader loader = ApplicationLoader$.MODULE$.apply(context);

        return loader.load(context).injector().instanceOf(Application.class);

    }

    @BeforeClass
    public static void startPlay() {
        app = provideApplication();
        Helpers.start(app);
    }

    @AfterClass
    public static void stopPlay() {
        if (app != null) {
            Helpers.stop(app);
            app = null;
        }
    }



    protected <T> T doInTransaction(EntityManagerCallback<T> callback) {
        EntityManagerFactory emf = BaseGlobal.ctx.getBean(EntityManagerFactory.class);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            T result = callback.call(em);

            tx.commit();

            return result;
        }
        catch (Exception e) {
            if ( tx != null && tx.isActive() ) tx.rollback();
            throw e; // or display error message
        }
        finally {
            em.close();
        }

    }

    protected <T> T doInTransactionWithGeneralDao(GeneralDaoCallback<T> callback) {
        return doInTransaction(em -> {
            GeneralDao generalDao = new GeneralDao(em);
            return callback.call(generalDao);
        });
    }


    @FunctionalInterface
    protected static interface EntityManagerCallback<T> {
        T call(EntityManager em);
    }

    @FunctionalInterface
    protected static interface GeneralDaoCallback<T> {
        T call(GeneralDao generalDao);
    }

//    public Result routeWithExceptionHandle(Http.RequestBuilder requestBuilder) {
//
//        Result result;
//        Http.RequestImpl req = requestBuilder.build();
//
//        try {
//            result = Helpers.route(requestBuilder);
//        } catch (Exception e) {
//            Global global = new Global();
//            return global.onError(req, e).get(3000000L);
//        }
//
//        String s = Helpers.contentAsString(result);
//        Logger.debug(String.format("request: %s, response: %s", req.toString(), s));
//        return result;
//    }

}
