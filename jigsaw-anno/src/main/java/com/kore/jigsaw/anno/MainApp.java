package com.kore.jigsaw.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author koreq
 * @date 2021-04-08
 * @description Application 相关注解，该注解标记主应用的 Application 类
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface MainApp {

}
