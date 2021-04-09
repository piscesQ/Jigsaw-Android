package com.kore.jigsaw.anno.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author koreq
 * @date 2021-04-08
 * @description 路由相关注解，自动注入属性
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface Autowired {
    String name() default "";               // 属性名

    boolean required() default false;       // 是否是必传参数

    String desc() default "";               // 描述
}
