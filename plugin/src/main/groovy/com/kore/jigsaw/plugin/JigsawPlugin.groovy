package com.kore.jigsaw.plugin

import com.android.build.gradle.internal.dependency.VariantDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.regex.Pattern

/**
 * @author koreq* @date 2021-04-09
 * @description gradle plugin 动态增加各个模块的依赖，该插件只能放在 com.android.application 中使用
 */
class JigsawPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        /*
        1、判断当前 task 是否是 assemble 任务，不是的话直接跳过
        2、如果是 assemble 任务，则获取当前 project 下的依赖的 library 列表，并动态添加依赖
        3、注册 transform 处理字节码
         */

        println("===Kore lifecycle JigsawPlugin apply start.....")
        def nameList = project.gradle.startParameter.taskNames
        def moduleName = project.path.replace(":", "")
        println("===Kore JigsawPlugin moduleName = ${moduleName}, nameList = ${nameList}")

        def taskInfo = getTaskInfo(nameList)            // 获取当前执行的 task 的属性
        if (taskInfo.isAssemble) {
            def depLibrary = getDepLibrary(project, taskInfo.isDebug)
            addDependencies(project, depLibrary)
            project.android.registerTransform(new JigsawTransform(project))
        }
        println("===Kore lifecycle JigsawPlugin apply end.....")
    }

    /**
     * 判断任务是否是 assemble 和 debug 类型
     *
     * @param taskNames
     * @return 返回 TaskInfo 对象
     */
    private TaskInfo getTaskInfo(List<String> taskNames) {
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
     * 获取 project 下的依赖列表
     *
     * @param project
     * @param isDebug 当前 task 是否是 debug 任务
     * @return 依赖列表
     */
    List<String> getDepLibrary(Project project, boolean isDebug) {
        String strDep = ""
        if (isDebug) {
            strDep = project.properties.get("debugComponent")
        } else {
            strDep = project.properties.get("compileComponent")
        }
        def depArr = strDep.split(",")
        def list = []
        for (String item : depArr) {
            list.add(item.trim())
        }
        return list
    }

    /**
     * 向 project 中动态添加依赖
     *
     * @param project
     * @param depList
     */
    void addDependencies(Project project, List<String> depList) {
        if (depList == null || depList.size() == 0) return
        for (String str : depList) {
            if (str.startsWith(":")) {
                str = str.substring(1)
            }

            def artifact = isMavenArtifact(str)
            if (artifact) {     // maven 格式的依赖
                project.dependencies.add(VariantDependencies.CONFIG_NAME_API, str)
            } else {            // 本地 libray 依赖
                project.dependencies.add(VariantDependencies.CONFIG_NAME_API, project.project(':' + str))
            }
        }
    }

    /**
     * 是否是 maven 格式的依赖，example: com.abc.efg:my-lib:1.0.1
     *
     * @param str
     * @return
     */
    static boolean isMavenArtifact(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return Pattern.matches("\\S+(\\.\\S+)+:\\S+(:\\S+)?(@\\S+)?", str);
    }

    private class TaskInfo {            // 当前 task 的属性
        boolean isAssemble = false
        boolean isDebug = false
    }
}