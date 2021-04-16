package com.kore.jigsaw.core.router;

import java.util.HashMap;
import java.util.Map;

/**
 * @author koreq
 * @date 2021-04-15
 * @description 模块对应的路由类，一一对应
 */
public abstract class BaseModuleRouter {
    protected Map<String, Class<?>> mPathMap = new HashMap<>();     // key: path; value: Activity Class
    protected Map<Class<?>, Map<String, Integer>> mParamMap = new HashMap<>();  // key: attr name; value: attr type

    public BaseModuleRouter() {
        addRouteTable();
    }

    /**
     * 模块的中配置的名称
     *
     * @return
     */
    public abstract String getModuleName();

    /**
     * 添加路由表
     */
    public abstract void addRouteTable();

    protected void addPathMap(String path, Class<?> clazz) {
        mPathMap.put(path, clazz);
    }

    protected void addParamMap(Class<?> clazz, String key, Integer type) {
        if (!mParamMap.containsKey(clazz)) {
            mParamMap.put(clazz, new HashMap<>());
        }
        Map<String, Integer> paramInfo = mParamMap.get(clazz);
        paramInfo.put(key, type);
    }

    public Class<?> findTargetClass(String path){
        return mPathMap.get(path);
    }

    public Map<String, Integer> findParamsInfo(Class<?> clazz){
        return mParamMap.get(clazz);
    }
}
