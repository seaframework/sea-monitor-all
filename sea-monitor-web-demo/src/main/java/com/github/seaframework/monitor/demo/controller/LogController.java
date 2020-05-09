package com.github.seaframework.monitor.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/5/3
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/log")
public class LogController {

    @GetMapping("/error")
    public Map<String, String> error() {
        log.debug("1111");
        log.info("2222");
        log.error("error");
        Map<String, String> map = new HashMap();
        return map;
    }
}
