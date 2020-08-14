# Dubbo监控

## 指标
|指标|描述|
|----|----|
|dubbo.exception.biz.count| 业务异常数 |
|dubbo.exception.forbidden.count| forbidden异常数 |
|dubbo.exception.network.count| network异常数|
|dubbo.exception.serialization.count| 序列化异常数|
|dubbo.exception.timeout.count| timeout异常数|
|dubbo.exception.unknown.count| unkown异常数|
|dubbo.thread.pool.active| dubbo线程池活跃数|
|dubbo.thread.pool.active.percent| dubbo线程池使用率 |
|dubbo.thread.pool.core| dubbo线程池核心线程数 |
|dubbo.thread.pool.max| dubbo线程池最大数 |
|dubbo.thread.pool.task| dubbo线程池任务执行数 |
|dubbo.cost| dubbo服务耗时（超10s上报）|
- ![](img/hot.png)当线程池使用率超过0.9时会自动dump 线程堆栈，目录`${user.home}/logs`

## 集成
### alibaba dubbo (2.7之前)

````
  META-INF/dubbo/com.alibaba.dubbo.rpc.Filter
  
  dubboExceptionMonitorFilter=com.github.seaframework.monitor.dubbo.alibaba.DubboExceptionMonitorFilter

````
  
### apache dubbo (2.7之后)

````
  META-INF/dubbo/org.apache.dubbo.rpc.Filter
  
  dubboExceptionMonitorFilter=com.github.seaframework.monitor.dubbo.apache.DubboExceptionMonitorFilter
````  


### 配置 dubbo filter

xml

````
<dubbo:provider filter="default,dubboExceptionMonitorFilter"/>
<dubbo:consumer filter="default,dubboExceptionMonitorFilter"/>
````

yml

````
dubbo.provider.filter= default,dubboExceptionMonitorFilter
dubbo.consumer.filter=default,dubboExceptionMonitorFilter

````