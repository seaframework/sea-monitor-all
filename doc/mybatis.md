# SQL监控

## 指标

|指标|描述|
|----|----|
|db.sql.error| sql执行异常上报|
|db.sql.error.count|一分钟中出错数|
|db.sql.large.record.error|返回记录数超5000上报|
|db.sql.cost|超5s的sql上报|

## 配置

````
  com.github.seaframework.monitor.mybatis.MybatisMonitorInterceptor
  <bean id="commonSqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
          <property name="dataSource" ref="commonDataSource" />
          <property name="configLocation" value="classpath:mybatis-config.xml" />
          <property name="mapperLocations" value="classpath*:commonSqlMap/*Mapper.xml" />
          <property name="plugins">
              <array>
                  <bean class="com.github.seaframework.monitor.mybatis.MybatisMonitorInterceptor"/>
              </array>
          </property>
      </bean>
````

`plugins`属性添加`com.github.seaframework.monitor.mybatis.MybatisMonitorInterceptor`