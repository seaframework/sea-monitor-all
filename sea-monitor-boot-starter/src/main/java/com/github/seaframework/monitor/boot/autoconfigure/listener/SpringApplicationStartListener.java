package com.github.seaframework.monitor.boot.autoconfigure.listener;

import com.github.seaframework.core.config.Configuration;
import com.github.seaframework.core.config.ConfigurationFactory;
import com.github.seaframework.core.util.StringUtil;
import com.github.seaframework.monitor.SeaMonitor;
import com.github.seaframework.monitor.boot.autoconfigure.SeaMonitorProperties;
import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.enums.MonitorModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/16
 * @since 1.0
 */
@Slf4j
public class SpringApplicationStartListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private AbstractApplicationContext ctx;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("init sea-monitor-boot-starter in application event");
        initSeaMonitor();
    }

    @Autowired
    private SeaMonitorProperties properties;

    @Value("${spring.application.name:unkown-app}")
    private String appName;

    private void initSeaMonitor() {
        if (properties == null) {
            log.info("sea monitor properties is null");
            SeaMonitor.disable();
            return;
        }

        if (properties.getMonitor() == null
                || properties.getMonitor().getEnabled() == null
                || !properties.getMonitor().getEnabled()
        ) {
            log.info("sea monitor is null or enable is null");
            SeaMonitor.disable();
            return;
        }
        log.info("init sea monitor config by boot starter");
        SeaMonitor.enable();
        String region = StringUtil.defaultIfEmpty(properties.getRegion(), "default");

        Configuration cfg = ConfigurationFactory.getInstance();

        Integer percent;
        if (properties.getSample() == null) {
            percent = MonitorConst.DEFAULT_SAMPLE_PERCENT;
        } else {
            percent = properties.getSample().getPercent();
            if (percent != null && percent >= 0 && percent <= 100) {
            } else {
                percent = MonitorConst.DEFAULT_SAMPLE_PERCENT;
            }
        }
        Integer periodTime;
        if (properties.getSend() == null) {
            periodTime = MonitorConst.DEFAULT_PUSH_PERIOD_TIME;
        } else {
            if (properties.getSend().getPeriod() == null) {
                periodTime = MonitorConst.DEFAULT_PUSH_PERIOD_TIME;
            } else {
                periodTime = properties.getSend().getPeriod().getTime();
                if (periodTime == null || periodTime <= 0) {
                    periodTime = MonitorConst.DEFAULT_PUSH_PERIOD_TIME;
                }
            }
        }

        String endpoint = properties.getMonitor().getEndpoint();
        if (StringUtil.isEmpty(endpoint)) {
            endpoint = appName;
        }
        Integer mode = properties.getMonitor().getMode();
        if (mode == null) {
            mode = MonitorModeEnum.REPORT.ordinal();
        }
        String traceExtensionName = properties.getMonitor().getTrace();
        if (StringUtil.isEmpty(traceExtensionName)) {
            traceExtensionName = StringUtil.EMPTY;
        }

        cfg.putString(MonitorConst.CONFIG_KEY_APP_NAME, appName);
        cfg.putString(MonitorConst.CONFIG_KEY_ENABLED, "true");
        cfg.putString(MonitorConst.CONFIG_KEY_REGION, region);
        cfg.putString(MonitorConst.CONFIG_KEY_MODE, "" + mode);
        cfg.putString(MonitorConst.CONFIG_KEY_TRACE, traceExtensionName);
        cfg.putString(MonitorConst.CONFIG_KEY_SAMPLE_PERCENT, "" + percent);
        cfg.putString(MonitorConst.CONFIG_KEY_ENDPOINT, endpoint);
        cfg.putString(MonitorConst.CONFIG_KEY_URI, properties.getMonitor().getUri());
        cfg.putString(MonitorConst.CONFIG_KEY_CONSUMER_COUNT, "" + MonitorConst.DEFAULT_CONSUMER_COUNT);
        cfg.putString(MonitorConst.CONFIG_KEY_SEND_ELEMENT_MAX_COUNT, "" + MonitorConst.DEFAULT_SEND_ELEMENT_MAX_COUNT);
        cfg.putString(MonitorConst.CONFIG_KEY_SEND_PERIOD_TIME, "" + periodTime);

        new Thread(() -> SeaMonitor.initialize()).start();

    }

}
