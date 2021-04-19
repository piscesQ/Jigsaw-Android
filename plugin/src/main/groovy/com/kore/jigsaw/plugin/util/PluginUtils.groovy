package com.kore.jigsaw.plugin.util

import com.kore.jigsaw.plugin.bean.TaskInfo
import org.gradle.api.Project

/**
 * @author koreq* @date 2021-04-19
 * @description gradle plugin 相关工具类
 */
public class PluginUtils {
    /**
     * 获取 project 下的依赖列表
     *
     * @param project
     * @param isDebug 当前 task 是否是 debug 任务
     * @return 依赖列表
     */
    public static List<String> getDepLibrary(Project project, boolean isDebug) {
        String strDep
        if (isDebug) {
            strDep = project.properties.get("debugComponent")
        } else {
            strDep = project.properties.get("releaseComponent")
        }
        def depArr = strDep.split(",")
        def list = []
        for (String item : depArr) {
            list.add(item.trim())
        }
        return list
    }

    /**
     * 判断任务是否是 assemble 和 debug 类型
     *
     * @param taskNames
     * @return 返回 TaskInfo 对象
     */
    public static TaskInfo getTaskInfo(List<String> taskNames) {
        TaskInfo taskInfo = new TaskInfo()
        for (String task : taskNames) {
            if (task.toUpperCase().contains("ASSEMBLE")
                    || task.contains("aR")
                    || task.contains("asR")
                    || task.contains("asD")
                    || task.toUpperCase().contains("TINKER")
                    || task.toUpperCase().contains("INSTALL")
                    || task.toUpperCase().contains("RESGUARD")) {
                if (task.toUpperCase().contains("DEBUG")) {
                    taskInfo.isDebug = true
                }
                taskInfo.isAssemble = true
                break
            }
        }
        return taskInfo
    }

    /**
     * 判断当前的 task 是否是 debug 版本
     *
     * @param taskNames
     * @return
     */
    public static boolean taskIsDebug(Project project) {
        def nameList = project.gradle.startParameter.taskNames
        TaskInfo info = getTaskInfo(nameList)
        return info.isDebug
    }
}