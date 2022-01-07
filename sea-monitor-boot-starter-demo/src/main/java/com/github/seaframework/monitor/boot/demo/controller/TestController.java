package com.github.seaframework.monitor.boot.demo.controller;

import com.github.seaframework.core.model.BaseResult;
import com.github.seaframework.core.util.RandomUtil;
import com.github.seaframework.monitor.SeaMonitor;
import com.github.seaframework.monitor.common.TagConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2022/1/7
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/monitor/biz")
    public BaseResult biz() {
        long value = RandomUtils.nextLong(1, 100);
        log.info("do biz value={}", value);

        MetricDTO dto = new MetricDTO();
        dto.setMetric("biz.error");
        dto.setValue(value);
        dto.addTag(TagConst.TAG1, "abc");
        dto.addTag(TagConst.TAG2, "service");
        Map<String, String> extra = new HashMap<>();
        extra.put("xx", "abc");
        extra.put("key1", "value1");
        extra.put("traceId", "c0a8d2cb1615258880547657" + RandomUtil.numeric(5));
        dto.setExtraMap(extra);
        SeaMonitor.logMetric(dto);

        return BaseResult.success();
    }
}
