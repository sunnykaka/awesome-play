package common.utils.scheduler;

import com.google.common.base.Stopwatch;
import play.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 定时任务父类, 注意子类要实现成单例类
 * Created by liubin on 15-6-4.
 */
public abstract class SchedulerTask implements Runnable {

    private AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public void run() {
        String className = getClass().getSimpleName();
        if(running.compareAndSet(false, true)) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                doRun();
            } catch (Throwable e) {
                Logger.error(className + "运行的时候发生错误: " + e.getMessage(), e);
            } finally {
                running.set(false);
                stopwatch.stop();
                Logger.info(className + "定时任务运行结束, 耗时: " + stopwatch.toString());
            }
        } else {
            Logger.warn(className + "定时任务已经在运行...");
        }
    }


    protected abstract void doRun();


}
