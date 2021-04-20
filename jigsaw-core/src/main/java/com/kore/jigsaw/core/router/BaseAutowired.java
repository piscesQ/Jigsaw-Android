package com.kore.jigsaw.core.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.kore.jigsaw.core.manager.GsonManager;

/**
 * @author koreq
 * @date 2021-04-16
 * @description 自动注解辅助类的基类
 */
public abstract class BaseAutowired {
    /**
     * 传入 Activity 对象，将 startActivity 所需的参数全部在该函数中进行赋值
     *
     * @param activity
     */
    public abstract void inject(Activity activity);

    /**
     * 传入 Bundle 对象，校验 bundle 中是否包含完整的必传参数
     *
     * @param bundle
     * @return
     */
    public abstract boolean preCheck(Bundle bundle);

    public boolean preDemo(Bundle bundle) {
        if (!bundle.containsKey("p1")) {
            return false;
        }
        if (!bundle.containsKey("p2")) {
            return false;
        }
        return true;
    }

    /**
     * 获取字符串，如果字段为空，则返回默认值
     *
     * @param intent
     * @param key
     * @param defaultValue
     * @return
     */
    protected String getNonNullStr(Intent intent, String key, String defaultValue) {
        String str = intent.getStringExtra(key);
        return TextUtils.isEmpty(str) ? defaultValue : str;
    }

    /**
     * 根据JSON字符串解析成对象，如果字符串为空，则返回默认值
     *
     * @param intent
     * @param key
     * @param defaultValue
     * @return
     */
    protected <T> T getObjFromJson(Intent intent, String key, Class<T> clazz, T defaultValue) {
        String str = intent.getStringExtra(key);
        T obj = null;
        try {
            obj = GsonManager.get().fromJson(str, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (obj == null) {
            return defaultValue;
        }
        return obj;
    }

    /**
     * 根据 key 获取序列化之后的对象，如果是空，则返回默认值
     *
     * @param intent
     * @param key
     * @param defaultValue
     * @return
     */
    protected <T> T getSerializableObj(Intent intent, String key, T defaultValue) {
        T serialObj = (T) intent.getSerializableExtra(key);
        if (serialObj == null) {
            return defaultValue;
        } else {
            return serialObj;
        }
    }

    /**
     * 根据 key 获取序列化之后的对象，如果是空，则返回默认值
     *
     * @param intent
     * @param key
     * @param defaultValue
     * @return
     */
    protected <T> T getParcelableObj(Intent intent, String key, T defaultValue) {
        T parcelableObj = intent.getParcelableExtra(key);
        if (parcelableObj == null) {
            return defaultValue;
        } else {
            return parcelableObj;
        }
    }
}
