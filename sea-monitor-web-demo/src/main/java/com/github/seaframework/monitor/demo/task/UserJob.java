package com.github.seaframework.monitor.demo.task;

import cn.hutool.core.util.RandomUtil;
import com.github.seaframework.monitor.SeaMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/28
 * @since 1.0
 */
@Slf4j
@Component
public class UserJob {

    //    @Scheduled(cron = "0/10 * * * * ?")
    public static void report() {
        log.info("report");
        SeaMonitor.logMetric("login", RandomUtil.randomInt(10, 100));
    }
}
