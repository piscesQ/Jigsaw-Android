package com.kore.jigsaw.plugin.bean

/**
 * @author koreq* @date 2021-04-10
 * @description ModuleApp 注解类的封装类
 */
class PriorityModuleApp {
    int priority = 0            // @ModuleApp 对应类的优先级，用于排序
    String className            // class 文件对应的名字

    PriorityModuleApp(String className) {
        this.className = className
    }


    @Override
    public String toString() {
        return "PriorityModuleApp{" +
                "priority=" + priority +
                ", className='" + className + '\'' +
                '}';
    }
}