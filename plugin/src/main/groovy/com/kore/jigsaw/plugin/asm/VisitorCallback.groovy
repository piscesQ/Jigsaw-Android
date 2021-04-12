package com.kore.jigsaw.plugin.asm

/**
 * @author koreq* @date 2021-04-10
 * @description visitor 的回调
 */
public class VisitorCallback {

    /**
     * 找到 Jigsaw 类
     */
    void visitJigsaw(String name) {}

    /**
     * 找到被注解 @MainApp 修饰的类
     */
    void visitMainApp(String name) {}

    /**
     * 找到被注解 @ModuleApp 修饰的类
     */
    void visitModuleApp(String name) {}

    /**
     * 遍历注解 @ModuleApp 的属性
     */
    void visitAnnoAttrs(String className, String attrName, Object attrValue) {}
}
