package com.kore.jigsaw.plugin.bean

/**
 * @author koreq* @date 2021-04-19
 * @description Task 的属性类
 */
public class TaskInfo {
    boolean isAssemble = false
    boolean isDebug = false

    @Override
    public String toString() {
        return "TaskInfo{" +
                "isAssemble=" + isAssemble +
                ", isDebug=" + isDebug +
                '}';
    }
}