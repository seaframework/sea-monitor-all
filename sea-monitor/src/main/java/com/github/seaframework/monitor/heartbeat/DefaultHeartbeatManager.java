package com.github.seaframework.monitor.heartbeat;

import com.github.seaframework.core.loader.EnhancedServiceLoader;
import com.github.seaframework.core.util.ListUtil;
import com.github.seaframework.monitor.SeaMonitor;
import com.github.seaframework.monitor.common.MonitorCommon;
import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.heartbeat.data.DataStatsCollector;
import com.github.seaframework.monitor.heartbeat.datasource.druid.DruidInfoCollector;
import com.github.seaframework.monitor.heartbeat.datasource.hikari.HikariInfoCollector;
import com.github.seaframework.monitor.heartbeat.dubbo.DubboLegacyThreadPoolHeartbeat;
import com.github.seaframework.monitor.heartbeat.dubbo.DubboThreadPoolHeartbeat;
import com.github.seaframework.monitor.heartbeat.http.HttpStatsCollector;
import com.github.seaframework.monitor.heartbeat.jvm.ClassLoadingInfoCollector;
import com.github.seaframework.monitor.heartbeat.jvm.JvmInfoCollector;
import com.github.seaframework.monitor.heartbeat.jvm.ThreadInfoCollector;
import com.github.seaframework.monitor.heartbeat.sys.SysAliveCollector;
import com.github.seaframework.monitor.heartbeat.tomcat.TomcatHttpStatsCollector;
import com.github.seaframework.monitor.heartbeat.tomcat.TomcatThreadPoolStatsCollector;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/3
 * @since 1.0
 */
@Slf4j
public class DefaultHeartbeatManager implements HeartbeatManager {

    private volatile boolean active = true;

    public DefaultHeartbeatManager() {
        JvmInfoCollector.getInstance().registerJVMCollector();

        StatusExtensionRegister register = StatusExtensionRegister.getInstance();
        register.register(new SysAliveCollector());
        register.register(new HttpStatsCollector());
        register.register(new DataStatsCollector());

        if (DruidInfoCollector.exist()) {
            register.register(new DruidInfoCollector());
        }
        if (HikariInfoCollector.exist()) {
            register.register(new HikariInfoCollector());
        }
        if (DubboLegacyThreadPoolHeartbeat.exist()) {
            register.register(new DubboLegacyThreadPoolHeartbeat());
        }
        if (DubboThreadPoolHeartbeat.exist()) {
            register.register(new DubboThreadPoolHeartbeat());
        }
        if (MonitorCommon.isTomcatContainer()) {
            register.register(new TomcatThreadPoolStatsCollector());
            register.register(new TomcatHttpStatsCollector());
        }

        register.register(new ClassLoadingInfoCollector());
        register.register(new ThreadInfoCollector());

        try {
            List<StatusExtension> statusExtension = EnhancedServiceLoader.loadAll(StatusExtension.class);
            if (ListUtil.isEmpty(statusExtension)) {
                return;
            }
            log.info("status extension size={}", statusExtension.size());
            statusExtension.stream().forEach(item -> {
                register.register(item);
            });
        } catch (Exception e) {
            log.error("fail to load extension", e);
        }

    }

    private void await() {
        // try to wait client init success
        try {
            Thread.sleep(10 * 1000L);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public void start() {
        await();
        SeaMonitor.logMetric(MonitorConst.METRIC_SYS_REBOOT, 1);

        while (active) {
            buildHeartbeat();

            try {
                Calendar cal = Calendar.getInstance();

                cal.set(Calendar.SECOND, 20);
                cal.add(Calendar.MINUTE, 1);

                long elapsed = cal.getTimeInMillis() - System.currentTimeMillis();

                if (elapsed > 0) {
                    Thread.sleep(elapsed);
                }
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private void buildHeartbeat() {
        StatusExtensionRegister res = StatusExtensionRegister.getInstance();
        List<StatusExtension> extensions = res.getStatusExtension();

        for (StatusExtension extension : extensions) {

            try {
                Map<String, Object> properties = extension.getProperties();

                if (properties != null && properties.size() > 0) {
                    properties.entrySet().stream().parallel().forEach(item -> {
                        if (item.getValue() instanceof MetricDTO) {
                            MetricDTO metricDTO = (MetricDTO) item.getValue();
                            metricDTO.setPeriodFlag(true);
                            SeaMonitor.logMetric(metricDTO);
                        } else if (item.getValue() instanceof List) {
                            try {
                                List<MetricDTO> data = (List<MetricDTO>) item.getValue();
                                if (ListUtil.isNotEmpty(data)) {
                                    data.stream().forEach(metric -> {
                                        metric.setPeriodFlag(true);
                                        SeaMonitor.logMetric(metric);
                                    });
                                }
                            } catch (Exception e) {
                                log.error("convert list error", e);
                            }
                        } else {
                            MetricDTO metricDTO = new MetricDTO();
                            metricDTO.setMetric(item.getKey());
                            metricDTO.setValue(Double.valueOf(item.getValue().toString()));
                            metricDTO.setPeriodFlag(true);
                            SeaMonitor.logMetric(metricDTO);
                        }
                    });
                }

            } catch (Exception e) {
                log.error("fail to collector metric", e);
            } finally {
            }
        }
    }

    @Override
    public void shutdown() {
        active = false;
    }

}
