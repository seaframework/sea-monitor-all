package com.github.seaframework.monitor.util;

import com.github.seaframework.core.util.NumberUtil;
import com.github.seaframework.monitor.vo.ThreadPoolStatus;
import lombok.extern.slf4j.Slf4j;

import java.math.RoundingMode;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/9
 * @since 1.0
 */
@Slf4j
public class ThreadPoolUtil {


    /**
     * get thread pool status
     *
     * @param tpe
     * @return
     */
    public static ThreadPoolStatus getStatus(ThreadPoolExecutor tpe) {
        ThreadPoolStatus status = new ThreadPoolStatus();
        if (tpe == null) {
            return status;
        }

        status.setMax(tpe.getMaximumPoolSize());
        status.setCore(tpe.getCorePoolSize());
        status.setLargest(tpe.getLargestPoolSize());
        status.setActive(tpe.getActiveCount());
        status.setTask(tpe.getTaskCount());
        status.setActivePercent(NumberUtil.divide(status.getActive(), status.getMax(), 3, RoundingMode.UP).doubleValue());
        return status;
    }
}
