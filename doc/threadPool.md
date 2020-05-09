# ThreadPool监控

## 指标

|指标|描述|
|----|----|
|xxx.max|线程池最大数|
|xxx.core|线程池核心数|
|xxx.largest|线程池最大数（到目前为止）|
|xxx.active|线程吃活跃数|
|xxx.task|线程池执行任务数|
|xxx.active.percent|线程池使用率|


## 集成

 手动集成


## 示例
  
 ````
   META-INF/service/com.github.seaframework.monitor.heartbeat.StatusExtension
 ````
  
````
  com.xxx.user.biz.sys.metric.UserThreadPoolStatusExtension
  /**
   * module name
   *
   * @author spy
   * @version 1.0 2020/4/9
   * @since 1.0
   */
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
              map.put(PREFIX  + ".max", status.getMax());
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