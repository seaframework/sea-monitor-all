package com.github.seaframework.monitor.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.github.seaframework.monitor.SeaMonitor;
import com.github.seaframework.monitor.demo.service.UserService;
import com.github.seaframework.monitor.dto.MetricDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/27
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/test/user")
public class UserController extends AbstractTestController {

    private static AtomicLong count = new AtomicLong(0);

    @Autowired
    private UserService userService;

    @GetMapping("/hi")
    public String hi() {

        try {
            Thread.sleep(RandomUtil.randomInt(1, 30) * 10);
        } catch (Exception e) {

        }
        log.info("----hi");
        return String.valueOf(count.addAndGet(1));
    }

    @GetMapping("/login")
    public String login() {
        MetricDTO metricDTO = new MetricDTO();
        metricDTO.setMetric("user.login");
        metricDTO.setValue(1);
        metricDTO.setTraceIdFlag(true);
        SeaMonitor.logMetric(metricDTO);

        SeaMonitor.logMetric("login", 1);
        return "success";
    }

    @GetMapping("/add/exception")
    public String addException() {
        userService.addException();
        return "success";
    }
}
