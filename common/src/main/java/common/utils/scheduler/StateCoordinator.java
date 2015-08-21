package common.utils.scheduler;

import akka.actor.Cancellable;
import akka.actor.Scheduler;
import common.exceptions.AppBusinessException;
import common.utils.RedisUtils;
import redis.clients.jedis.Jedis;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 当部署多个应用服务器的时候，这个类能够做到在某一时刻，只有一个应用服务器在执行定时任务。
 * 解决办法是运行一个定时任务，每隔一定时间，使用Redis set命令的CAS功能设置值，如果设置成功，
 * 代表当前没有其他服务器在执行任务，则自己执行，否则跳过。
 * 自己执行的时候需要定时刷新key的超时时间（续租）
 *
 * Created by liubin on 15-7-24.
 */
public class StateCoordinator extends SchedulerTask {

    private String cacheKey;

    //租期，单位秒
    public static final int LEASE_TIME = 60;

    private Scheduler scheduler;
    private ExecutionContextExecutor dispatcher;

    private Cancellable selfScheduler;
    private List<Cancellable> schedulers = new ArrayList<>();

    private List<Task> tasks = new ArrayList<>();

    private volatile boolean schedulersRunning = false;

    public String uid = UUID.randomUUID().toString();

    private StateCoordinator(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public static StateCoordinator create(String cacheKey) {
        return new StateCoordinator(RedisUtils.buildKey("state_coordinator", cacheKey));
    }


    public void start(Scheduler scheduler, ExecutionContextExecutor dispatcher) {
        this.scheduler = scheduler;
        this.dispatcher = dispatcher;

        //启动自己的定时器
        if(selfScheduler == null) {
            selfScheduler = scheduler.schedule(
                    Duration.create(10, TimeUnit.SECONDS),
                    Duration.create(LEASE_TIME / 2, TimeUnit.SECONDS),
                    this,
                    dispatcher
            );
        }
    }

    public void stop() {
        selfScheduler.cancel();
        selfScheduler = null;
        schedulers.forEach(Cancellable::cancel);
        schedulers.clear();
        tasks.clear();
        schedulersRunning = false;
    }

    public void addScheduler(Optional<FiniteDuration> initialDelay,  Optional<FiniteDuration> interval,
                             SchedulerTask schedulerTask) {
        tasks.add(new Task(initialDelay, interval, schedulerTask));
    }

    @Override
    protected void doRun() {
        RedisUtils.withJedisClient(jedis -> {

            if(schedulersRunning) {
                //续租
                renewLease(jedis);

            } else {

                if(isSetOk(jedis.set(cacheKey, uid, "NX", "EX", LEASE_TIME))) {
                    //创建租约成功，启动定时器
                    startSchedulers();

                }

            }

            return null;
        });
    }

    private void startSchedulers() {
        schedulers = tasks.stream().map(task -> task.schedule(scheduler, dispatcher)).collect(Collectors.toList());
        schedulersRunning = true;
    }

    /**
     * 续租
     * @param jedis
     */
    private void renewLease(Jedis jedis) {
        String result = jedis.set(cacheKey, uid, "XX", "EX", LEASE_TIME);
        if(!isSetOk(result)) {
            //可能其他应用进程已经开启了定时任务，出现这种情况有可能是当前应用压力过大导致没有按时续租。
            //先停止任务，具体原因待查。
            schedulers.forEach(Cancellable::cancel);
            schedulersRunning = false;
            throw new AppBusinessException(String.format("设置state_coordinator时发现返回的result不为OK，result: %s, 代码有bug?", result));
        }
    }

    private boolean isSetOk(String result) {
        return result != null && result.equals("OK");
    }


    static class Task {
        Optional<FiniteDuration> initialDelay;
        Optional<FiniteDuration> interval;
        SchedulerTask schedulerTask;

        /**
         *
         * @param initialDelay 如果为None, 表示项目启动后马上启动
         * @param interval 如果为None, 表示定时任务只执行一次
         * @param schedulerTask
         */
        public Task(Optional<FiniteDuration> initialDelay, Optional<FiniteDuration> interval, SchedulerTask schedulerTask) {
            this.initialDelay = initialDelay;
            this.interval = interval;
            this.schedulerTask = schedulerTask;
        }

        /**
         * 启动定时任务
         * @param scheduler
         * @param dispatcher
         * @return
         */
        public Cancellable schedule(Scheduler scheduler, ExecutionContextExecutor dispatcher) {

            FiniteDuration initial = initialDelay.orElse(Duration.create(5, TimeUnit.SECONDS));
            if(interval.isPresent()) {
                return scheduler.schedule(initial, interval.get(), schedulerTask, dispatcher);
            } else {
                return scheduler.scheduleOnce(initial, schedulerTask, dispatcher);
            }
        }

    }

}
