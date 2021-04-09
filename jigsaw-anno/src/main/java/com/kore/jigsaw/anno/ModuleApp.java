package com.kore.jigsaw.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author koreq
 * @date 2021-04-08
 * @description Application 相关注解，该注解标记 Module 的 Application 类
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ModuleApp {
    int priority() default 10000;       // 优先级，越大越先调用，默认值为中优先级
}
