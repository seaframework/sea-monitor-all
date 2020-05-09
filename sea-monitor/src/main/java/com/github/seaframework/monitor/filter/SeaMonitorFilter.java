package com.github.seaframework.monitor.filter;

import com.github.seaframework.monitor.SeaMonitor;
import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.common.TagConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.enums.CounterEnum;
import com.github.seaframework.monitor.heartbeat.data.DataStats;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/24
 * @since 1.0
 */
@Slf4j
public class SeaMonitorFilter implements Filter {
    private Set<String> excludeUrls;
    private Set<String> excludePrefixes;
    // common is 501
    private static final String CLIENT_ABORT_EXCEPTION = "org.apache.catalina.connector.ClientAbortException";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Sea Monitor Filter init");

        String exclude = filterConfig.getInitParameter("exclude");

        if (exclude != null) {
            excludeUrls = new HashSet<>();
            String[] excludeUrls = exclude.split(";");

            for (String s : excludeUrls) {
                int index = s.indexOf("*");

                if (index > 0) {
                    if (excludePrefixes == null) {
                        excludePrefixes = new HashSet<>();
                    }
                    excludePrefixes.add(s.substring(0, index));
                } else {
                    this.excludeUrls.add(s);
                }
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("sea monitor do filter");
        }

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();
        boolean exclude = excludePath(path);

        if (exclude) {
            log.debug("exclude url");

            chain.doFilter(request, response);
            return;
        }

        logMetric(chain, req, res);
    }

    @Override
    public void destroy() {

    }

    private boolean excludePath(String path) {
        try {
            boolean exclude = excludeUrls != null && excludeUrls.contains(path);

            if (!exclude && excludePrefixes != null) {
                for (String prefix : excludePrefixes) {
                    if (path.startsWith(prefix)) {
                        exclude = true;
                        break;
                    }
                }
            }
            return exclude;
        } catch (Exception e) {
            return false;
        }
    }


    protected void logMetric(FilterChain chain, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("logMetric");
        }

        long start = System.currentTimeMillis();
        int status = 0;

        try {
            chain.doFilter(request, response);
            status = 200;
        } catch (ServletException e) {
            log.error("http500", e);
            status = 500;
            throw e;
        } catch (IOException e) {
            log.error("http501", e);
            status = 501;
            throw e;
        } catch (Throwable e) {
            log.error("http500", e);
            status = 500;
            throw new RuntimeException(e);
        } finally {
            long cost = System.currentTimeMillis() - start;

            logUri(request, status, cost);
            logRequestCost(request, status, cost);
        }
    }

    private void logUri(HttpServletRequest request, int status, long cost) {
        if (!SeaMonitor.isEnabled()) {
            return;
        }
        if (hitRule(status)) {
            return;
        }

        Map<String, String> tagMap = new HashMap<>();
        tagMap.put(TagConst.PROTOCOL, MonitorConst.HTTP);
        tagMap.put(TagConst.HTTP_STATUS, "" + status);

        MetricDTO dto = new MetricDTO();
        dto.setMetric(request.getRequestURI());
        dto.setValue(cost);
        dto.setTagsMap(tagMap);
        enhanceMetric(dto);

        SeaMonitor.logMetric(dto);
        SeaMonitor.logCount(CounterEnum.HTTP500.getKey());
        logSystemError();
    }

    private void logRequestCost(HttpServletRequest request, int status, long cost) {
        if (!SeaMonitor.isEnabled()) {
            return;
        }
        if (hitRule(status, cost)) {
            return;
        }

        Map<String, String> tagMap = new HashMap<>();
        tagMap.put(TagConst.PROTOCOL, MonitorConst.HTTP);
        tagMap.put(TagConst.HTTP_STATUS, "" + status);
        tagMap.put(TagConst.URI, request.getRequestURI());

        MetricDTO dto = new MetricDTO();
        dto.setMetric(MonitorConst.METRIC_HTTP_REQUEST_TIME);
        dto.setValue(cost);
        dto.setTagsMap(tagMap);
        enhanceMetric(dto);

        SeaMonitor.logMetric(dto);
    }

    private long MAX_REQUEST_COST = 30 * 1000;

    /**
     * 判断是否需要上报数据
     *
     * @param status
     * @return
     */
    protected boolean hitRule(int status) {
        return status == 200;
    }

    /**
     * 判断是否需要上报数据
     *
     * @param status
     * @param cost
     * @return
     */
    protected boolean hitRule(int status, long cost) {
        return status == 200 && cost < MAX_REQUEST_COST;
    }


    /**
     * @param dto
     */
    protected void enhanceMetric(MetricDTO dto) {
        if (dto == null) {
            return;
        }
        dto.setTraceIdFlag(true);
        dto.setStep(10);
    }

    protected void logSystemError() {
        DataStats dataStats = DataStats.currentStatsHolder();
        dataStats.logCount(CounterEnum.SYS_ERROR);
    }

}
