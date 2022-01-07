package com.github.seaframework.monitor.heartbeat.dubbo;

import com.github.seaframework.core.util.ClassUtil;
import com.github.seaframework.core.util.ListUtil;
import com.github.seaframework.core.util.MapUtil;
import com.github.seaframework.core.util.ReflectUtil;
import com.github.seaframework.monitor.common.TagConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.util.ThreadPoolUtil;
import com.github.seaframework.monitor.vo.ThreadPoolStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.threadpool.manager.DefaultExecutorRepository;
import org.apache.dubbo.common.threadpool.manager.ExecutorRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Override
    protected Map<String, Object> doStatics() {

        try {
            ExecutorRepository executorRepository = ExtensionLoader.getExtensionLoader(ExecutorRepository.class).getDefaultExtension();

            if (executorRepository instanceof DefaultExecutorRepository) {
                DefaultExecutorRepository defaultExecutorRepository = (DefaultExecutorRepository) executorRepository;

                //String componentKey = EXECUTOR_SERVICE_COMPONENT_KEY;
                //        if (CONSUMER_SIDE.equalsIgnoreCase(url.getParameter(SIDE_KEY))) {
                //            componentKey = CONSUMER_SIDE;
                //        }
                // data的key是固定的，要么是 EXECUTOR_SERVICE_COMPONENT_KEY 要么是 CONSUMER_SIDE
                ConcurrentMap<String, ConcurrentMap<Integer, ExecutorService>> data = (ConcurrentMap<String, ConcurrentMap<Integer, ExecutorService>>) ReflectUtil.read(defaultExecutorRepository, "data");

                //provider
                ConcurrentMap<Integer, ExecutorService> executors = data.get(CommonConstants.EXECUTOR_SERVICE_COMPONENT_KEY);
                if (executors != null) {
                    List<MetricDTO> metrics = new ArrayList<>();

                    executors.forEach((port, executor) -> {
                        if (executor instanceof ThreadPoolExecutor) {
                            ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
                            ThreadPoolStatus status = ThreadPoolUtil.getStatus(tpe);

                            Map<String, String> tags = new HashMap<>(1, 1);
                            tags.put(TagConst.TAG1, "" + port);

                            metrics.add(buildMetric("dubbo.thread.pool.max", status.getMax(), tags));
                            metrics.add(buildMetric("dubbo.thread.pool.core", status.getCore(), tags));
                            metrics.add(buildMetric("dubbo.thread.pool.largest", status.getLargest(), tags));
                            metrics.add(buildMetric("dubbo.thread.pool.active", status.getActive(), tags));
                            metrics.add(buildMetric("dubbo.thread.pool.task", status.getTask(), tags));
                            metrics.add(buildMetric("dubbo.thread.pool.active.percent", status.getActivePercent(), tags));

                            if (status.getMax() < Integer.MAX_VALUE) {
                                checkDumpStack(status.getActivePercent());
                            }
                        }
                    });
                    if (ListUtil.isNotEmpty(metrics)) {
                        Map<String, Object> retMap = new HashMap<>(1);
                        retMap.put("data", metrics);
                        return retMap;
                    }

                }

            } else {
                log.warn("unchecked thread pool implement. Plz contact developer.");
            }

        } catch (Exception e) {
            log.error("fail to collector apache dubbo thread pool info.", e);
        }

        return MapUtil.empty();
    }
}
