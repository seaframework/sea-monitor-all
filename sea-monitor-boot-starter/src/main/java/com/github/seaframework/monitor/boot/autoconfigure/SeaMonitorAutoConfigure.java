package com.github.seaframework.monitor.boot.autoconfigure;

import com.github.seaframework.core.util.ArrayUtil;
import com.github.seaframework.core.util.ListUtil;
import com.github.seaframework.core.util.StringUtil;
import com.github.seaframework.monitor.aop.SeaMonitorAspect;
import com.github.seaframework.monitor.boot.autoconfigure.listener.SpringApplicationStartListener;
import com.github.seaframework.monitor.filter.SeaMonitorFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/16
 * @since 1.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(com.github.seaframework.monitor.boot.autoconfigure.SeaMonitorProperties.class)
public class SeaMonitorAutoConfigure {

    @Bean
    public SpringApplicationStartListener seaMonitorSpringApplicationStartListener() {
        return new SpringApplicationStartListener();
    }

    @Value("${sea.monitor.filter.exclude:}")
    private String exclude;

    @Value("${sea.monitor.filter.urlPatterns:/*}")
    private List<String> urlPatterns;

    @Value("${sea.monitor.filter.order:1}")
    private Integer filterOrder;

    @Bean
    @ConditionalOnProperty(name = "sea.monitor.filter.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean seaMonitorFilter() {
        log.info("init sea monitor filter bean");
        SeaMonitorFilter seaMonitorFilter = new SeaMonitorFilter();

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(seaMonitorFilter);

        if (StringUtil.isNotEmpty(exclude)) {
            registration.addInitParameter("exclude", exclude);
        }
        if (ListUtil.isEmpty(urlPatterns)) {
            registration.addUrlPatterns("/*");
        } else {
            registration.addUrlPatterns(ArrayUtil.toArray(urlPatterns));
        }
        registration.setName("sea-monitor-filter");
        registration.setOrder(filterOrder);  //值越小，Filter越靠前。
        return registration;
    }

    @Bean
    @ConditionalOnProperty(name = "sea.monitor.enabled", havingValue = "true", matchIfMissing = true)
    public SeaMonitorAspect seaMonitorAspect() {
        return new SeaMonitorAspect();
    }

}
