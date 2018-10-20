# cat-mock

## 目的
本项目用于为单元测试时mock提供支持,让单元测试可以mock模拟被间接多层级调用的服务,可用于mock dubbo,webservice,http,mq等所有程序调用到的外部服务和中间件

## 用途和意义
使用本mock工具,配合内存数据库,将可以让单元测试关注于真正的业务处理,不再需要分层进行内部业务逻辑的mock,只要将外部依赖的接口和中间件使用本工具进行mock再将数据库使用内存数据库mock,就可以让单元测试覆盖到整个内部业务逻辑流程,完全模拟程序实际运行时的情况

## 特点
可以和easymock,mockito,mockit三种主流mock框架集成,功能测试用例可以按照大家熟悉的单元测试用例写法进行编写,没有额外的学习负担


## 使用方式
mock 测试工具 [使用说明](../readme.md)



## 全局catmock工具示例
这个工具,提供了mock spring上下文环境中,任意位置代码的功能,并支持手动指定mock内容
用法:
### catMock.mockAll(this);     
将测试用例中引用的spring注入对象扫描,并自动替换对象中和@Mock注解相同的属性,,注意:替换的属性需和测试用例引用的对象有调用层级上的联系
### catMock.addMock(SampleSecondService.class, "sampleOtherSysFacade", sampleOtherSysFacade);
手动替换某个类中的某个属性,注意:替换的类需和测试用例引用的对象有调用层级上的联系

<br>[代码示例](../cat-mock/src/test/java/com/s1coder/cat/mock/CatMockTest.java)

## spring配置mock辅助标签
这个工具可以用来帮助创建完全独立的spring启动环境,可以模拟redis,mq,dubbo等任何需要外部链接的spring bean
<br>[测试示例](../cat-mock/src/test/resources/spring-init-test.xml)
<br>[模拟dubbo配置示例](sample/spring-dubbo-memdb-test.xml)
<br>[模拟mq配置示例](sample/spring-mq-memdb-test.xml)
<br>[模拟redis配置示例](sample/redis-cluster-memdb-test.xml)