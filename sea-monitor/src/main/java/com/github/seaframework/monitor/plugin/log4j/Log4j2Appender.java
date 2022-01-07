package com.github.seaframework.monitor.plugin.log4j;

import com.github.seaframework.monitor.SeaMonitor;
import com.github.seaframework.monitor.enums.CounterEnum;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

@Plugin(name = "SeaMonitorAppender", category = "Core", elementType = "appender", printObject = true)
public class Log4j2Appender extends AbstractAppender {

    private static final long serialVersionUID = 2705802038361151598L;

    private Log4j2Appender(String name, Filter filter, Layout<? extends Serializable> layout,
                           final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        try {
            Level level = event.getLevel();

            if (level.isMoreSpecificThan(Level.ERROR)) {
                logError(event);
            }
        } catch (Exception ex) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(ex);
            }
        }
    }

    @PluginFactory
    public static Log4j2Appender createAppender(@PluginAttribute("name") String name,
                                                @PluginElement("Layout") Layout<? extends Serializable> layout, @PluginElement("Filter") final Filter filter,
                                                @PluginAttribute("otherAttribute") String otherAttribute) {
        if (name == null) {
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new Log4j2Appender(name, filter, layout, true);
    }

    private void logError(LogEvent event) {
//        ThrowableProxy info = event.getThrownProxy();
//
//        if (info != null) {
        SeaMonitor.logCount(CounterEnum.SYS_LOG_COUNT.getKey());
//        }
    }

}