package com.github.seaframework.monitor;

import com.alibaba.fastjson.JSON;
import com.github.seaframework.core.config.Configuration;
import com.github.seaframework.core.config.ConfigurationFactory;
import com.github.seaframework.core.loader.EnhancedServiceLoader;
import com.github.seaframework.core.util.*;
import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.common.TagConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.enums.CounterEnum;
import com.github.seaframework.monitor.enums.MonitorModeEnum;
import com.github.seaframework.monitor.heartbeat.HeartbeatManager;
import com.github.seaframework.monitor.heartbeat.StatusExtension;
import com.github.seaframework.monitor.heartbeat.data.DataStats;
import com.github.seaframework.monitor.heartbeat.impl.DefaultHeartbeatManager;
import com.github.seaframework.monitor.message.MessageProducer;
import com.github.seaframework.monitor.message.simple.SimpleMessageProducer;
import com.github.seaframework.monitor.samplers.PercentageBasedSampler;
import com.github.seaframework.monitor.samplers.Sampler;
import com.github.seaframework.monitor.samplers.SamplerProperties;
import com.github.seaframework.monitor.trace.TraceExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Sea Monitor
 *
 * @author spy
 * @version 1.0 2020/3/20
 * @since 1.0
 */
public final class SeaMonitor {

    private static final Logger log = LoggerFactory.getLogger(SeaMonitor.class);

    private static MonitorModeEnum monitorModeEnum;
    private static volatile boolean init = false;
    private static volatile boolean enabled = false;
    private static volatile boolean dubboMonitorEnabled = true;
    private static volatile boolean datasourceMonitorEnabled = true;

    private static Sampler sampler;

    private static String localIp;
    private static String endpoint;
    private static String app;
    private static String region;

    private static MessageProducer producer;
    private static HeartbeatManager heartbeatManager;
    private static TraceExtension traceExtension;

    private static final SeaMonitor instance = new SeaMonitor();

    private SeaMonitor() {
    }

    public static SeaMonitor getInstance() {
        return instance;
    }

    private static void checkAndInitialize() {
        try {
            if (!init) {
                initializeInternal();
            }
        } catch (Exception e) {
            errorHandler(e);
        }
    }


    public static void enable() {
        enabled = true;
    }

    public static void disable() {
        enabled = false;
    }

    public static void enableDubboMonitor() {
        dubboMonitorEnabled = true;
    }

    public static void enableDatasourceMonitor() {
        datasourceMonitorEnabled = true;
    }

    public static boolean isDubboMonitorEnabled() {
        return isEnabled() && dubboMonitorEnabled;
    }

    public static boolean isDatasourceMonitorEnabled() {
        return isEnabled() && datasourceMonitorEnabled;
    }


    public static void initialize() {
        checkAndInitialize();
    }


    private static void initCommon() {
        if (monitorModeEnum == null) {
            monitorModeEnum = MonitorModeEnum.REPORT;
        }
        localIp = NetUtil.getLocalIp();
        Configuration cfg = ConfigurationFactory.getInstance();

        // load from file
        Properties properties = PropertiesUtil.load(MonitorConst.MONITOR_CONFIG_FILE);
        String percent, traceExtensionName;
        if (!properties.isEmpty()) {
            log.info("load from properties");
            String enable = properties.getProperty(MonitorConst.CONFIG_KEY_ENABLED, "false");
            region = properties.getProperty(MonitorConst.CONFIG_KEY_REGION, "default");
            String monitorMode = properties.getProperty(MonitorConst.CONFIG_KEY_MODE, "0");
            percent = properties.getProperty(MonitorConst.CONFIG_KEY_SAMPLE_PERCENT, "100");
            app = properties.getProperty(MonitorConst.CONFIG_KEY_ENDPOINT, "unknown-endpoint");
            String uri = properties.getProperty(MonitorConst.CONFIG_KEY_URI, MonitorConst.DEFAULT_COLLECTOR_URI);
            String consumerCount = properties.getProperty(MonitorConst.CONFIG_KEY_CONSUMER_COUNT, "" + MonitorConst.DEFAULT_CONSUMER_COUNT);
            String sendElementMaxCount = properties.getProperty(MonitorConst.CONFIG_KEY_SEND_ELEMENT_MAX_COUNT, "" + MonitorConst.DEFAULT_SEND_ELEMENT_MAX_COUNT);
            String sendPeriodTime = properties.getProperty(MonitorConst.CONFIG_KEY_SEND_PERIOD_TIME, "" + MonitorConst.DEFAULT_PUSH_PERIOD_TIME);
            endpoint = buildEndpoint(region, app, localIp, true);

            monitorModeEnum = MonitorModeEnum.of(monitorMode);
            traceExtensionName = properties.getProperty(MonitorConst.CONFIG_KEY_TRACE, "");

            cfg.putString(MonitorConst.CONFIG_KEY_APP_NAME, app);
            cfg.putString(MonitorConst.CONFIG_KEY_ENABLED, enable);
            cfg.putString(MonitorConst.CONFIG_KEY_REGION, region);
            cfg.putString(MonitorConst.CONFIG_KEY_MODE, monitorMode);
            cfg.putString(MonitorConst.CONFIG_KEY_SAMPLE_PERCENT, percent);
            cfg.putString(MonitorConst.CONFIG_KEY_ENDPOINT, endpoint);
            cfg.putString(MonitorConst.CONFIG_KEY_URI, uri);
            cfg.putString(MonitorConst.CONFIG_KEY_CONSUMER_COUNT, consumerCount);
            cfg.putString(MonitorConst.CONFIG_KEY_SEND_ELEMENT_MAX_COUNT, sendElementMaxCount);
            cfg.putString(MonitorConst.CONFIG_KEY_SEND_PERIOD_TIME, sendPeriodTime);
        } else {
            log.info("load from config");
            app = cfg.getString(MonitorConst.CONFIG_KEY_ENDPOINT);
            if (StringUtil.isEmpty(app)) {
                log.warn("endpoint is null so disable");
                disable();
                return;
            }
            cfg.putString(MonitorConst.CONFIG_KEY_APP_NAME, app);
            region = cfg.getString(MonitorConst.CONFIG_KEY_REGION, "default");
            endpoint = buildEndpoint(region, app, localIp, true);
            cfg.putString(MonitorConst.CONFIG_KEY_ENDPOINT, endpoint);
            percent = cfg.getString(MonitorConst.CONFIG_KEY_SAMPLE_PERCENT, "100");

            String monitorMode = cfg.getString(MonitorConst.CONFIG_KEY_MODE, "0");
            monitorModeEnum = MonitorModeEnum.of(monitorMode);

            traceExtensionName = cfg.getString(MonitorConst.CONFIG_KEY_TRACE, "");
        }

        buildTraceExtension(traceExtensionName);

        SamplerProperties samplerProperties = new SamplerProperties();
        samplerProperties.setPercentage(Integer.valueOf(percent));
        sampler = new PercentageBasedSampler(samplerProperties);

        producer = new SimpleMessageProducer();
        producer.init();
        heartbeatManager = new DefaultHeartbeatManager();

        new Thread(() -> {
            heartbeatManager.start();
        }).start();


        Thread shutdownThread = new Thread(() -> {
            heartbeatManager.shutdown();
        });
        shutdownThread.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }


    private static void initializeInternal() {
        validate();

        if (isEnabled()) {
            try {
                if (!init) {
                    synchronized (instance) {
                        if (!init) {
                            initCommon();
                            if (isEnabled()) {

                                log.info("Sea Monitor is lazy initialized!");
                                init = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("fail to initialize sea monitor", e);
                disable();
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isInitialized() {
        return init;
    }

    /**
     * 计算数量
     * 1min统计
     *
     * @param metric
     */
    public static void logCount(String metric) {
        if (isEnabled()) {
            DataStats dataStats = DataStats.currentStatsHolder();
            dataStats.logCount(metric);
        }
    }

    /**
     * 业务指标
     *
     * @param metric
     * @param value
     */
    public static void logMetric(String metric, double value) {
        logMetric(metric, value, null, null);
    }

    /**
     * 业务指标
     *
     * @param metric
     * @param value
     * @param tagKey
     * @param tagValue
     */
    public static void logMetric(String metric, double value, String tagKey, String tagValue) {
        logMetric(metric, value, tagKey, tagValue, null, null);
    }

    /**
     * 业务指标
     *
     * @param metric
     * @param value
     * @param tag1Key
     * @param tag1Value
     * @param tag2Key
     * @param tag2Value
     */
    public static void logMetric(String metric, double value, String tag1Key, String tag1Value, String tag2Key, String tag2Value) {
        if (isEnabled()) {
            MetricDTO dto = new MetricDTO();
            dto.setMetric(metric);
            dto.setValue(value);

            Map<String, String> map = new HashMap<>(2);
            if (StringUtil.isNotEmpty(tag1Key, tag1Value)) {
                map.put(tag1Key, tag1Value);
            }

            if (StringUtil.isNotEmpty(tag2Key, tag2Value)) {
                map.put(tag2Key, tag2Value);
            }
            dto.setTagsMap(map);
            logMetric(dto);
        }
    }

    /**
     * 业务指标
     *
     * @param dto
     */
    public static void logMetric(MetricDTO dto) {
        if (isEnabled()) {
            checkAndInitialize();

            try {
                if (!isLogMode() && !sampler.sample().isSampled()) {
                    return;
                }
                if (dto == null || StringUtil.isEmpty(dto.getMetric())) {
                    return;
                }
                push(dto);

                if (dto.isErrorFlag()) {
                    logError();
                }
            } catch (Exception e) {
                errorHandler(e);
            }
        }
    }

    /**
     * 错误类型加入全局系统错误统计
     *
     * @param metric
     */
    public static void logErrorCount(String metric) {
        if (isEnabled()) {
            DataStats dataStats = DataStats.currentStatsHolder();
            dataStats.logCount(metric);
            dataStats.logCount(CounterEnum.SYS_ERROR);
        }
    }

    /**
     * 1min级别统计错误率
     * aggregate into memory
     */
    public static void logError() {
        if (isEnabled()) {
            DataStats dataStats = DataStats.currentStatsHolder();
            dataStats.logCount(CounterEnum.SYS_ERROR);
        }
    }


    private static void validate() {
        String enable = System.getProperty("SEA_MONITOR_ENABLED", "true");

        if ("false".equals(enable)) {
            log.info("SeaMonitor is disable due to system environment SEA_MONITOR_ENABLED is false.");
            enabled = false;
        } else {

            Map<String, String> map = PropertiesUtil.loadForMap(MonitorConst.MONITOR_CONFIG_FILE);

            String customDomain = null;
            if (map.isEmpty()) {
                Configuration cfg = ConfigurationFactory.getInstance();
                customDomain = cfg.getString(MonitorConst.CONFIG_KEY_ENDPOINT);
            } else {
                customDomain = MapUtil.getString(map, MonitorConst.CONFIG_KEY_ENDPOINT);
            }

            if (StringUtil.isEmpty(customDomain)) {
                log.info("SeaMonitor is disable due to no app name in resource file sea.monitor.properties");
                enabled = false;
            }

        }

    }

    private static void errorHandler(Exception e) {
        //TODO fixme
        log.error("error handler", e);
    }


    public static MonitorModeEnum getMonitorModeEnum() {
        return monitorModeEnum;
    }

    public static void setMonitorModeEnum(MonitorModeEnum monitorModeEnum) {
        SeaMonitor.monitorModeEnum = monitorModeEnum;
    }


    /**
     * load enable config.
     *
     * @return
     */
    public static boolean getEnabledConfig() {
        Properties properties = PropertiesUtil.load(MonitorConst.MONITOR_CONFIG_FILE);
        String enabled;
        if (!properties.isEmpty()) {
            log.info("load from properties");
            enabled = properties.getProperty(MonitorConst.CONFIG_KEY_ENABLED, "false");
        } else {
            log.info("load from config");
            Configuration cfg = ConfigurationFactory.getInstance();
            enabled = cfg.getString(MonitorConst.CONFIG_KEY_ENABLED, "false");
        }
        boolean enabledFlag = BooleanUtil.isTrue(enabled);
        log.info("sea monitor enabled={}", enabledFlag);
        return enabledFlag;
    }

    /**
     * 是否日志模式
     *
     * @return
     */
    private static boolean isLogMode() {
        return monitorModeEnum == MonitorModeEnum.LOG;
    }


    private static void push(MetricDTO dto) {
        enhance(dto);
        if (dto.getStep() <= 0) {
            dto.setStep(60);
        }
        dto.setEndpoint(endpoint);
        if (dto.getTimestamp() <= 0) {
            dto.setTimestamp(System.currentTimeMillis() / 1000);
        }
        if (isLogMode()) {
            log.info("{}", JSON.toJSONString(dto));
        } else {
            producer.push(dto);
        }
    }

    private static void enhance(MetricDTO dto) {
        if (dto == null) {
            return;
        }

        Map<String, String> tags;
        if (MapUtil.isEmpty(dto.getTagsMap())) {
            tags = new HashMap<>();
            dto.setTagsMap(tags);
        } else {
            tags = dto.getTagsMap();
        }

        tags.put(TagConst.REGION, region);
        tags.put(TagConst.INSTANCE, localIp);
        tags.put(TagConst.APP, app);

        if (dto.isTraceIdFlag()) {
            Map<String, String> extraMap = dto.getExtraMap();
            if (MapUtil.isEmpty(extraMap)) {
                extraMap = new HashMap<>();
                dto.setExtraMap(extraMap);
            }
            if (traceExtension != null) {
                try {
                    extraMap.put(TagConst.TRACE_ID, traceExtension.getTraceId());
                } catch (Exception e) {
                    log.error("fail to get traceId, plz check");
                }
            }
        }
    }

    private static String buildEndpoint(final String region, final String app, final String localIp, boolean oneService) {
        if (StringUtil.isEmpty(app)) {
            return app;
        }
        String endpoint = app;
        if (StringUtil.isNotEmpty(region)) {
            endpoint = region + "_" + app;
        }

        if (oneService) {
            return endpoint;
        }
        if (!endpoint.endsWith(localIp)) {
            return endpoint + "_" + localIp;
        }
        return endpoint;
    }

    private static void buildTraceExtension(String traceExtensionName) {
        try {
            if (StringUtil.isEmpty(traceExtensionName)) {
                return;
            }
            StatusExtension ext = EnhancedServiceLoader.load(StatusExtension.class);
            traceExtension = EnhancedServiceLoader.load(TraceExtension.class, traceExtensionName);
        } catch (Exception e) {
            traceExtension = null;
            log.error("fail to get trace extension[loadLevel={}]", traceExtensionName);
        }
    }

}
