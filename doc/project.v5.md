# 工程集成

## spring-boot类工程

pom.xml

````
  <properties>
		<!--最新版本请从仓库获取 https://search.maven.org/search?q=g:com.github.seaframework -->
	   <sea.monitor.version>[3.0.0, 3.0.999]</sea.monitor.version>
  </properties>
  
  <dependencies>
      <dependency>
          <groupId>com.github.seaframework</groupId>
          <artifactId>sea-monitor-boot-starter</artifactId>
          <version>${sea.monitor.version}</version>
      </dependency>
  </dependencies> 
````  

application.yml

````
  sea:
    # 区域
    region: default
    env: pro
    monitor:
      # 需要手动开启
      enabled: true
      # 应用名称
      endpoint: user
      uri: http://127.0.0.1:19000
      # log mode
      # 0 report to remote server (by default)
      # 1 log to local log file
      mode: 0
      sample:
      	# 采样率
        percent: 100
````  

完整示例，请参考`sea-monitor-all/sea-monitor-boot-starer-demo`工程

## 非spring-boot类工程

### pom.xml

````
  <properties>
  		<!--最新版本请从仓库获取 https://search.maven.org/search?q=g:com.github.seaframework -->
  		<sea.monitor.version>1.0.0</sea.monitor.version>
  </properties>
  
  <dependencies>
		<dependency>
		  <groupId>com.github.seaframework</groupId>
		  <artifactId>sea-monitor</artifactId>
		  <version>${sea.monitor.version}</version>
		</dependency>
  <dependencies>
````

### sea.monitor.properties

在工程目录resource放sea.monitor.properties文件，内容如下

````
  #
  # 区域
  sea.region=default
  # envirmonent, such as dev,test,pro
  sea.env=pro
  # sea monitor enable or not
  sea.monitor.enabled=true
  # application name
  sea.monitor.endpoint=user // 修改成当前系统名称
  # log mode
  # 0 report to remote server (by default)
  # 1 log to local log file
  sea.monitor.mode=0
  # sea monitor uri
  sea.monitor.uri=http://127.0.0.1:2058/api/collector/push
  # 采样率
  sea.monitor.sample.percent=100
````

完整示例，请参考`sea-monitor-all/sea-monitor-web-demo`工程

### 配置Filter

WEB-INF/web.xml

````  
  <filter>
      <filter-name>sea-monitor</filter-name>
      <filter-class>com.github.seaframework.monitor.filter.SeaMonitorFilter</filter-class>
  </filter>
  <filter-mapping>
      <filter-name>sea-monitor</filter-name>
      <url-pattern>/*</url-pattern>
  </filter-mapping>
````

`/*`这类拦截了所有请求，其实吧，像`/api/*`这种才是最好的，你开心就好

### spring AOP （可选）

使用@SeaMonitorTrace 注解

metric：指标名称

````
  <bean class="com.github.seaframework.monitor.aop.SeaMonitorAspect"/>
  		
  @Override
  @SeaMonitorTrace(metric = "user.add.exception")
  public void addException() {
      throw new NullPointerException();
  }
````

## 手动集成

> 适用于任意Java项目工程，手写的才是最好的！不要相信轮子！
>
>

````
	String region = "";
	String enabled = "";
	String endpoint = "";
	String uri = "";
	
	Configuration cfg = ConfigurationFactory.getInstance();
	cfg.putString(MonitorConst.CONFIG_KEY_REGION, region);
	cfg.putString(MonitorConst.CONFIG_KEY_ENABLED, enabled);
	cfg.putString(MonitorConst.CONFIG_KEY_ENDPOINT, endpoint);
	cfg.putString(MonitorConst.CONFIG_KEY_URI, uri);
	
	if (BooleanUtil.isTrue(enabled)) {
	  SeaMonitor.enable();
	  new Thread() {
	    @Override
	    public void run() {
	      SeaMonitor.initialize();
	    }
	  }.start();
	} else {
	  SeaMonitor.disable();
	}
````

  