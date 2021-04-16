# 组件化框架 - jigsaw
## 各部分设计
### gradle插件
1、各业务模块解耦，在编译过程中，动态添加依赖的 module 
2、找的 Jigsaw 类，初始化各个模块的 @ModuleApp 类，在 Jigsaw 的构造方法中，按照优先级从大到小的顺序添加到 mModuleAppList 
3、找到 MainApplication，遍历其各个方法，并在对应方法中调用 Jigsaw 的对应方法
4、找到 JRouter 类，并将被 @RouteTable 标记的类初始化后，插入到 JRouter 的 List<BaseModuleRouter> 中

### 核心库 - 负责注册接口下沉的类、路由解析跳转
1、Jigsaw
（1）map<Interface.class, Object>
     在 module 的 application 中进行注册和删除操作
（2）将 module 的 app 的对象注册到该类
    使用 gradle 插件，将其注入到 Jigsaw 中，并将该部分代码注入到 MainApp 中
2、JRouter
（1）包含 List<BaseModuleRouter> ，每个 module 与 BaseModuleRouter 的实现类一一对应
（2）包含构造方法，在 transform 中，将所有 module 对应的 BaseModuleRouter 的实现类，初始化，并插入到该方法中；TODO 按照优先级排序
（3）包含 verifyUri ，用于验证传入的 path 和 对应参数列表是否合法
（4）包含 openUri，用于跳转到指定页面，需要支持 onActivityForResult

### 注解
1、路由
（1）@Route
（2）@Autowired
（3）@RouteTable  用于标记生成的路由表，在 plugin transform 中做标记使用
2、Application
（1）@ModuleApp
（2）@MainApp  
        
### router-apt
1、处理被 @Autowired 修饰的属性，并生成辅助类，包含两个方法 inject（为属性赋值），preCheck（校验必传属性）；实现同一接口，用于keep
2、处理被 @Route 修饰的类，

### 独立运行
1、创建独立运行的 application module，可以不修改 library 的任何内容，符合开闭原则
2、在独立运行的 application 中，初始化其他模块接口的 mock 类，并注册到 Jigsaw 中，用于处理单独运行时的跨模块调用

### TODO 
1、aar支持
2、注解的范围改成 Source 测试
3、RouteProcessor 去掉 autowired 测试
4、页面跳转是否需要登陆
5、页面跳转是否需要安全模式