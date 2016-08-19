import com.google.inject.AbstractModule;
import common.utils.play.BaseGlobal;
import common.utils.play.GuiceModule;
import common.utils.play.GuiceSpringModule;
import utils.Global;

/**
 * Created by liubin on 2016/8/12.
 */
public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(GuiceModule.class).asEagerSingleton();
        bind(GuiceSpringModule.class).asEagerSingleton();

        bind(BaseGlobal.class).to(Global.class).asEagerSingleton();
    }
}

