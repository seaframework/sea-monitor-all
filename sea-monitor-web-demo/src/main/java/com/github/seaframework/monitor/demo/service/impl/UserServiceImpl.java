package com.github.seaframework.monitor.demo.service.impl;

import com.github.seaframework.monitor.annotation.SeaMonitorTrace;
import com.github.seaframework.monitor.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/3
 * @since 1.0
 */
@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {
    @Override
    public void add() {
        log.info("---");
    }

    @Override
    @SeaMonitorTrace(metric = "user.add.exception")
    public void addException() {
        throw new NullPointerException();
    }
}
