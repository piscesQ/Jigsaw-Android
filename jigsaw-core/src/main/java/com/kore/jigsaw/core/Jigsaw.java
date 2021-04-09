package com.kore.jigsaw.core;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author koreq
 * @date 2021-04-08
 * @description Jigsaw 核心类
 */
public class Jigsaw {
    // 存放各个 module 的接口和实现类的对应关系
    private Map<Class<?>, Class<?>> mRelationMap = new HashMap<>();
    // 存放各个 module 的 接口实现类的对象
    private SoftReference<HashMap<Class<?>, Object>> mServiceImplMap = new SoftReference<>(new HashMap<Class<?>, Object>());
    // 存放各个 module 的 Application 的对象
    private List<Application> mModuleAppList = new ArrayList<>();

    /**
     * 根据接口类获取其实现类
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static synchronized <T> T getService(Class<T> clazz) {       // TODO kore 处理类型转换
        Object obj = get().getSoftRef().get(clazz);
        T tObj = (T)obj;
        if (tObj == null) {
            Class<?> targetClass = get().mRelationMap.get(clazz);
            try {
                tObj = (T) targetClass.newInstance();
                get().getSoftRef().put(clazz, tObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tObj;
    }

    /**
     * 将对外接口的实现类和下沉接口一一对应，并存入 mRelationMap 中
     *
     * @param superClazz 超类
     * @param childClazz 子类
     * @return
     */
    public static synchronized void addRelation(Class<?> superClazz, Class<?> childClazz) {
        get().mRelationMap.put(superClazz, childClazz);
    }

    public HashMap<Class<?>, Object> getSoftRef() {
        if (mServiceImplMap == null) {
            mServiceImplMap = new SoftReference<>(new HashMap<Class<?>, Object>());
        }
        return mServiceImplMap.get();
    }

    private Jigsaw() {      // 使用插件，在该方法中将 module 的 application 按照优先级添加到 mModuleAppList
    }

    public static Jigsaw get() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        static final Jigsaw INSTANCE = new Jigsaw();
    }

    public void attachBaseContext(Context context) {
        for (Application app : mModuleAppList) {
            try {
                // invoke each application's attachBaseContext 该方法是 protected 的方法，所以需要反射
                Method attachBaseContext = ContextWrapper.class.getDeclaredMethod("attachBaseContext", Context.class);
                attachBaseContext.setAccessible(true);
                attachBaseContext.invoke(app, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onCreate() {
        for (Application app : mModuleAppList) {
            app.onCreate();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        for (Application app : mModuleAppList) {
            app.onConfigurationChanged(configuration);
        }
    }

    public void onLowMemory() {
        for (Application app : mModuleAppList) {
            app.onLowMemory();
        }
    }

    public void onTerminate() {
        for (Application app : mModuleAppList) {
            app.onTerminate();
        }
    }

    public void onTrimMemory(int level) {
        for (Application app : mModuleAppList) {
            app.onTrimMemory(level);
        }
    }
}
