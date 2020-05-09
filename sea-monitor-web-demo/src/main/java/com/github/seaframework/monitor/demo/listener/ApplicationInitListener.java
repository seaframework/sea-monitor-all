package com.github.seaframework.monitor.demo.listener;

import com.github.seaframework.monitor.SeaMonitor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/27
 * @since 1.0
 */
@Slf4j
public class ApplicationInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        SeaMonitor.enable();
        SeaMonitor.initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
