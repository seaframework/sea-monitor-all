## Tomcat监控

## 指标
|指标|描述|
|----|----|
|tomcat.http.request.count| 请求数 |
|tomcat.http.error.count| error数量 |
|tomcat.http.processing.time| 处理时间 |
|tomcat.http.max.time| http最大处理时间 |
|tomcat.http.bytes.received| http接收数据量大小 |
|tomcat.http.bytes.sent| http发送数据量大小 |
|tomcat.thread.pool.busy| tomcat线程池活跃数 |
|tomcat.thread.pool.max| tomcat线程池最大值 |
|tomcat.thread.pool.busy.percent| tomcat线程池使用率，超过0.9可以告警 |

## 配置
  无需配置，自动探测