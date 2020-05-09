package com.github.seaframework.monitor.heartbeat.dubbo;

import com.github.seaframework.core.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/3
 * @since 1.0
 */
@Slf4j
public class DubboThreadPoolHeartbeat extends AbstractDubboThreadPoolHeartbeat {

    private static final String THREAD_POOL_CHECK_CLASS = "org.apache.dubbo.rpc.protocol.dubbo.status.ThreadPoolStatusChecker";

    public static boolean exist() {
        return ClassUtil.load(THREAD_POOL_CHECK_CLASS) != null;
    }

    @Override
    public String getThreadPoolName() {
        return THREAD_POOL_CHECK_CLASS;
    }

}
