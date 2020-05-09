package com.github.seaframework.monitor.demo.custom;

import cn.hutool.core.util.RandomUtil;
import com.github.seaframework.monitor.heartbeat.StatusExtension;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/3
 * @since 1.0
 */
@Slf4j
public class MyThreadPoolStatusExtension implements StatusExtension {
    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getId() {
        return "user";
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> map = new HashMap<>();
        map.put("random", RandomUtil.randomInt(1, 1000));
        return map;
    }
}
