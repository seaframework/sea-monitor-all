package com.github.seaframework.monitor.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/16
 * @since 1.0
 */
@Data
@ConfigurationProperties("sea")
public class SeaMonitorProperties {

    private String region;

    @NestedConfigurationProperty
    private Monitor monitor;

    @NestedConfigurationProperty
    private Sample sample;

    @NestedConfigurationProperty
    private Send send;

    @NestedConfigurationProperty
    private Filter filter;

    @Data
    public static class Monitor {
        private Boolean enabled;
        private String endpoint;
        private Integer mode;
        private String trace;
        private String uri;
    }

    @Data
    public static class Sample {
        private Integer percent;
    }

    @Data
    public static class Send {
        @NestedConfigurationProperty
        private Period period;
    }

    @Data
    public static class Period {
        private Integer time;
    }

    @Data
    public static class Filter {
        // 是否启用，默认启用
        private Boolean enabled;
        // 拦截的url
        private List<String> urlPatterns;
        // 排除的url
        private String exclude;
        // filter order
        private Integer order;
    }
}
