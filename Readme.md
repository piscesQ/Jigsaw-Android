# 组件化框架 - jigsaw
## 各部分设计
### gradle插件
1、各业务模块解耦，在编译过程中，动态添加依赖的 module 
2、将 module 的 application 中 onCreate 等方法，按照优先级顺序插入到 MainApplication 中

### 核心库 - 负责注册接口下沉的类 
1、map<Interface.class, Object>
（1） 在 module 的 application 中进行注册和删除操作
2、将 module 的 app 的对象注册到该类
（1）使用 gradle 插件，将其注入到 Jigsaw 中，并将该部分代码注入到 MainApp 中

### 注解
1、路由
（1）@Router
（2）@Autowired
2、Application
（1）@ModuleApp
（2）@MainApp  
        
### router-apt
1、使用注解处理器，将处理路由信息，生成辅助类

### 独立运行
1、创建独立运行的application module，可以不修改 library 的任何内容，符合开闭原则