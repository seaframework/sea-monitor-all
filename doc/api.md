#  API

## 单个指标

````  
      MetricDTO metricDTO = new MetricDTO();
      metricDTO.setMetric(MetricEnum.HTTP_REQUEST_ERROR.getKey());
      metricDTO.setValue(1);
      metricDTO.setErrorFlag(true);
      metricDTO.setTraceIdFlag(true);
      SeaMonitor.logMetric(metricDTO);
````

## 统计指标

````
     SeaMonitor.logCount(MetricEnum.HTTP_REQUEST_ERROR_COUNT.getKey());
````

## SPI扩展指标
> 适用于定制化周期性指标上报，统计周期1min

````
META-INF/service/com.github.seaframework.monitor.heartbeat.StatusExtension
  实现接口com.github.seaframework.monitor.heartbeat.StatusExtension
````

````
  public Map<String, Object> getProperties()
````
  - 接口map支持普通key value
  - map支持返回value是List<MetricDTO>


  示例

````
  @Slf4j
  public class UserThreadPoolStatusExtension implements StatusExtension {
      private static final String PREFIX = "user.thread.pool";
      @Override
      public String getDescription() {
          return PREFIX;
      }
      @Override
      public String getId() {
          return PREFIX;
      }
      @Override
      public Map<String, Object> getProperties() {
          Map<String, Object> map = new HashMap<>();
          Map<String, ThreadPoolExecutor> poolExecutorMap = ThreadPoolExecutorUtil.getInstance();
          if (MapUtil.isEmpty(poolExecutorMap)) {
              return map;
          }
          poolExecutorMap.forEach((key, value) -> {
              ThreadPoolStatus status = ThreadPoolUtil.getStatus(value);
              map.put(PREFIX + ".max", status.getMax());
              map.put(PREFIX + ".core", status.getCore());
              map.put(PREFIX + ".largest", status.getLargest());
              map.put(PREFIX + ".active", status.getActive());
              map.put(PREFIX + ".task", status.getTask());
              map.put(PREFIX + ".active.percent", status.getActivePercent());
          });
          return map;
      }
  }
````