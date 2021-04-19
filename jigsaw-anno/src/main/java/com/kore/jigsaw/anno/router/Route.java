package com.kore.jigsaw.anno.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author koreq
 * @date 2021-04-08
 * @description 路由相关注解，路由节点
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Route {
    String path();                  // 路径

    String desc() default "";       // 描述
}
