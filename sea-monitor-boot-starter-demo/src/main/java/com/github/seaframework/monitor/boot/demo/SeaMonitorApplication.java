package com.github.seaframework.monitor.boot.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/16
 * @since 1.0
 */
@Slf4j
@SpringBootApplication
public class SeaMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeaMonitorApplication.class, args);
    }
}
