package utils;

import common.utils.play.BaseGlobal;
import common.utils.play.GuiceModule;
import common.utils.play.GuiceSpringModule;
import play.Application;
import play.Logger;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;


public class Global extends BaseGlobal {

    @Inject
    public Global(Application application,
                  ApplicationLifecycle lifecycle,
                  GuiceModule guiceModule,
                  GuiceSpringModule guiceSpringModule) {
        super(application, lifecycle, guiceModule, guiceSpringModule);
    }

    @Override
    protected void runSchedulers() {
        Logger.debug("run schedulers...");
    }

    @Override
    protected void stopSchedulers() {
        Logger.debug("stop schedulers...");
    }

}