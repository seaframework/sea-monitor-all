package com.github.seaframework.monitor.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/27
 * @since 1.0
 */
@Slf4j
public class MonitorPushScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MonitorPushScheduler.class);

    private static final ScheduledExecutorService MonitorScheduler = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "sea-monitor-push-scheduler");
                }

            });


    public static void init() {
        logger.info("MonitorPushScheduler start");
//        MonitorScheduler.scheduleAtFixedRate(new MonitorPushRunner(), MonitorConst.PUSH_PERIOD_TIME,
//                MonitorConst.PUSH_PERIOD_TIME, TimeUnit.SECONDS);
    }

//    static class MonitorPushRunner implements Runnable {
//
//        @Override
//        public void run() {
//            try {
//                Map<String, Number> monitors = Maps.newHashMap();
//                FalconMonitor.generateBusinessMetrics(monitors);
//                MonitorSendUtil.send(monitors);
//            } catch (Exception e) {
//                logger.error("run MonitorPushRunner exception", e);
//            }
//        }
//    }

    public static void destroy() {
        MonitorScheduler.shutdownNow();
    }

}