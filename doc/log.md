# Log

## Log4j

## Logback

用于统计通过error级别的错误量
logback.xml

````
    <appender name="SEA_MONITOR" class="com.github.seaframework.monitor.logback.SeaMonitorAppender"></appender>

<root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="SEA_MONITOR"/>
    </root>
    
````