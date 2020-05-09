package com.github.seaframework.monitor;

import com.alibaba.fastjson.JSON;
import com.github.seaframework.monitor.dto.MetricDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/24
 * @since 1.0
 */
@Slf4j
public class SeaMonitorTest {

    @Before
    public void before() throws Exception {
        SeaMonitor.enable();
    }

    @Test
    public void run16() throws Exception {

//        Tags tags = Tags.of(TagConst.INSTANCE, "11");
//
//        tags.and(TagConst.INSTANCE, "11");
//
//
//        System.out.println(tags);
    }


    @Test
    public void run31() throws Exception {
        SeaMonitor.enable();

        SeaMonitor.logMetric("abc", 1);
    }

    @After
    public void after() throws Exception {
        TimeUnit.MINUTES.sleep(5);
    }


    @Test
    public void run51() throws Exception {
        MetricDTO dto = new MetricDTO();

        dto.setErrorFlag(false);
        dto.setMetric("metric.11");
        dto.setValue(1);

        Map<String, String> extraMap = new HashMap<>();
        extraMap.put("key", "value");
        dto.setExtraMap(extraMap);

        System.out.println(JSON.toJSONString(dto));
    }

}
