#  Http监控
  • 服务器内部异常
  • 长耗时（超过30s）

  
  
## 指标
  
|指标|描述|
|----|----|
|http.500.count| 服务器未捕获的异常|
|http.request.cost.time|请求耗时，超过30s|

  
## 配置

  - 非spring-boot工程配置参考web.xml中Filter内容
  - spring-boot工程无需配置  