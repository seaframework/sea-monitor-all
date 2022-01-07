package com.github.seaframework.monitor.common;

import com.github.seaframework.core.util.ClassUtil;
import com.github.seaframework.core.util.DateUtil;
import com.github.seaframework.monitor.heartbeat.dubbo.DubboLegacyThreadPoolHeartbeat;
import com.github.seaframework.monitor.heartbeat.dubbo.DubboThreadPoolHeartbeat;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/18
 * @since 1.0
 */
@Slf4j
public class MonitorCommon {

    /**
     * 应用启动时间
     */
    private static String uptime = "";

    public static void setUptime() {
        Date now = new Date();
        uptime = DateUtil.toString(now, DateUtil.DEFAULT_FORMAT);
    }

    public static String getUptime() {
        return uptime;
    }


    private static volatile Boolean hasDubboCheckFlag = false;
    private static volatile Boolean hasDubboFlag = null;

    public static boolean hasDubbo() {
        if (hasDubboCheckFlag) {
            return hasDubboFlag;
        }
        hasDubboFlag = DubboThreadPoolHeartbeat.exist() || DubboLegacyThreadPoolHeartbeat.exist();
        hasDubboCheckFlag = true;
        return hasDubboFlag;
    }

    private static volatile boolean isTomcatCheckFlag = false;
    private static volatile boolean isTomcatFlag = false;
    private static final String CLAZZ_TOMCAT_CONTAINER = "org.apache.catalina.Container";

    public static boolean isTomcatContainer() {
        if (isTomcatCheckFlag) {
            return isTomcatFlag;
        }
        isTomcatFlag = ClassUtil.load(CLAZZ_TOMCAT_CONTAINER) != null;
        isTomcatCheckFlag = true;
        return isTomcatFlag;
    }

}
