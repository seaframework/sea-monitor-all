## Redis连接池监控 

## 指标

|指标|描述|
|----|----|
|redis.pool.active.count|连接池活跃数|
|redis.pool.idle.count| 连接池空闲数 |
|redis.pool.waiter.count| 连接池等待数|
|redis.pool.total.count| 连接池总数|
|redis.pool.active.percent|连接池使用率 |

## 集成
手动集成

## 示例

````
  META-INF/service/com.github.seaframework.monitor.heartbeat.StatusExtension
````

````
  @Slf4j
  public class RedisStatsCollector implements StatusExtension {
      @Override
      public String getDescription() {
          return getId();
      }
      @Override
      public String getId() {
          return "sea.redis.stats.collector";
      }
      @Override
      public Map<String, Object> getProperties() {
          log.debug("collect redis info");
          Map<String, Object> map = new HashMap<>();
          List<MetricDTO> data = new ArrayList<>();
          String beanName = "";
          try {
              beanName = "redisCacheService";
              JedisPoolConfig poolConfig = SpringContextHolder.getBean("poolConfig");
              JedisConnectionFactory connectionFactory = SpringContextHolder.getBeanSafe("jedisConnectionFactory");
              Object poolObj = ReflectUtil.read(connectionFactory, "pool");
              if (poolObj != null) {
                  Pool pool = (Pool) poolObj;
                  RedisMonitorInfo monitorInfo = new RedisMonitorInfo();
                  monitorInfo.setServiceName(beanName);
                  monitorInfo.setActiveCount(pool.getNumActive());
                  monitorInfo.setIdleCount(pool.getNumIdle());
                  monitorInfo.setWaiterCount(pool.getNumWaiters());
                  monitorInfo.setTotalCount(poolConfig.getMaxTotal());
                  convertToMetricList(monitorInfo, data);
              }
          } catch (Exception e) {
              log.error("fail to collect redis info", e);
          }
 
          map.put("data", data);
          return map;
      }
      private void convertToMetricList(RedisMonitorInfo monitorInfo, List<MetricDTO> data) {
          addMetric(data, MonitorConst.METRIC_REDIS_ACTIVE_COUNT, monitorInfo.getActiveCount(), TagConst.SERVICE, monitorInfo.getServiceName());
          addMetric(data, MonitorConst.METRIC_REDIS_IDLE_COUNT, monitorInfo.getIdleCount(), TagConst.SERVICE, monitorInfo.getServiceName());
          addMetric(data, MonitorConst.METRIC_REDIS_WAITER_COUNT, monitorInfo.getWaiterCount(), TagConst.SERVICE, monitorInfo.getServiceName());
          addMetric(data, MonitorConst.METRIC_REDIS_TOTAL_COUNT, monitorInfo.getTotalCount(), TagConst.SERVICE, monitorInfo.getServiceName());
          addMetric(data, MonitorConst.METRIC_REDIS_ACTIVE_PERCENT, NumberUtil.divide(monitorInfo.getActiveCount(), monitorInfo.getTotalCount(), 3, RoundingMode.UP).doubleValue(), TagConst.SERVICE, monitorInfo.getServiceName());
      }
      private void addMetric(List<MetricDTO> data, String key, double value, String tag1Key, String tag2Value) {
          MetricDTO metricDTO = new MetricDTO();
          metricDTO.setMetric(key);
          metricDTO.setValue(value);
          Map<String, String> tagsMap = new HashMap<>();
          tagsMap.put(tag1Key, tag2Value);
          metricDTO.setTagsMap(tagsMap);
          data.add(metricDTO);
      }
  }
````