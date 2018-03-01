# mr-frontier mr前置系统

mr-frontier 集成了 springcloud zuul， dubbo， 并开放了灵活的业务扩展接口。 
前置系统处于内部系统和外部系统之间， 不仅保护内部系统安全，还会做业务处理的扩展。
* 1、前置与外部系统互访均通过http
* 2、前置与内部系统互访可通过rest方式（http协议），也可通过rpc调用（tcp协议或者http协议）
* 3、外部系统与内部系统交互必须通过前置系统

mr-frontier-service 是springboot工程，直接启动即可
演示地址为: http://106.14.195.171:8086,  替换下面的本机地址 http://localhost:8888可直接看到效果 

# 外部下系统交互
* 以/openapi 开头为内部系统访问外部的前缀
* 内部访问外部api示例: http://localhost:8888/openapi/weather?city=上海
* mr-frontier
* zuul.routes.example.path=/openapi/weather/**
* zuul.routes.example.url=http://www.sojson.com/open/api/weather/json.shtml

# 内部系统交互 rest方式
* 以/mr/rest 开头为内部系统restful接口规范，配置如下，访问示例： http://localhost:8888/mr/rest/say 
* zuul.routes.mrrest.path=/mr/rest/**
* zuul.routes.mrrest.url=http://localhost:8888/

# 内部系统交互 rpc方式
* 以/mr/rest 开头为内部系统restful接口规范，配置如下，访问示例： http://localhost:8888/mr/rpc/say 
* zuul.routes.mrrpc.path=/mr/rpc/**
* zuul.routes.mrrpc.url=http://localhost:8888/

# dubbo 配置：
* spring.dubbo.application.name=provider
* spring.dubbo.registry.address=zookeeper://127.0.0.1:2181
* spring.dubbo.protocol.name=dubbo
* spring.dubbo.protocol.port=20880
* spring.dubbo.scan=com.mr.framework.frontier.service
# dubbo测试方法
* 当前的dubbo服务注释掉了，如果用的话去掉注释打开即可
* 测试方法：去掉当前的dubbo服务注释，启动安装好的zookeeper，再启动 mr-frontier-dubbo-consumer这个springboot工程。
* 访问：http://localhost:8081/save， 它将会发起rpc远程调用 

# admin(管理控制台)配置：
演示地址：http://106.14.195.171:8085，提供了前置系统的界面化的管理功能
