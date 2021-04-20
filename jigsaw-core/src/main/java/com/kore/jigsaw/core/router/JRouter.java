package com.kore.jigsaw.core.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.kore.jigsaw.anno.utils.Constants;
import com.kore.jigsaw.anno.utils.TypeKind;
import com.kore.jigsaw.core.bean.RouterResult;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author koreq
 * @date 2021-04-15
 * @description 路由核心类
 */
public class JRouter {
    private static final String TAG = JRouter.class.getSimpleName();
    private List<BaseModuleRouter> mModuleList = new ArrayList<>();

    private JRouter() {
    }

    public static JRouter get() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        static final JRouter INSTANCE = new JRouter();
    }

    /**
     * 在 Activity 的 onCreate 中调用，用于自动给 @Autowired 字段进行赋值
     *
     * @param activity
     */
    public void inject(Activity activity) {
        // 找到该类的辅助类 $$JRouter$$Autowired ，使用反射进行然后初始化，并调用其中的 inject 方法
        Class<? extends Activity> clazz = activity.getClass();
        String fullName = clazz.getName();
        try {
            Class<?> helperClass = Class.forName(fullName + Constants.SUFFIX_AUTOWIRED);
            Method inject = helperClass.getMethod("inject", Activity.class);
            inject.setAccessible(true);
            Object instance = helperClass.getConstructor().newInstance();
            inject.invoke(instance, activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查必要参数
     *
     * @param target
     * @param paramsInfo
     * @param bundle
     * @return
     */
    public boolean preCheck(Class<?> target, Map<String, Integer> paramsInfo, Bundle bundle) {
        // 找到该类的辅助类 $$JRouter$$Autowired ，使用反射进行然后初始化，并调用其中的 preCheck 方法
        if (paramsInfo == null || paramsInfo.size() == 0)
            return true;        // 如果跳转参数列表为 null 则返回 true
        String fullName = target.getName();
        try {
            Class<?> helperClass = Class.forName(fullName + Constants.SUFFIX_AUTOWIRED);
            Method preCheck = helperClass.getMethod("preCheck", Bundle.class);
            preCheck.setAccessible(true);
            Object instance = helperClass.getConstructor().newInstance();
            Object result = preCheck.invoke(instance, bundle);
            if (result instanceof Boolean) {
                return (boolean) result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public RouterResult openUri(Context context, String path, Bundle bundle) {
        return openUri(context, path, bundle, 0);
    }

    public RouterResult openUri(Context context, String path) {
        return openUri(context, path, new Bundle(), 0);
    }

    public RouterResult openUri(Context context, String path, Bundle bundle, int requestCode) {
        if (TextUtils.isEmpty(path) || !path.startsWith("jigsaw://")) {
            String msg = "path is invalid!";
            Log.e(TAG, msg);
            return new RouterResult(false, msg);
        }
        Uri pathUri = Uri.parse(path);
        String uriHost = pathUri.getHost();
        String uriPath = pathUri.getPath();
        for (BaseModuleRouter moduleRouter : mModuleList) {
            if (uriHost.equals(moduleRouter.getModuleName())) {
                Class<?> target = moduleRouter.findTargetClass(uriPath);
                if (target == null) {
                    String msg = "can't find target class!";
                    Log.e(TAG, msg);
                    return new RouterResult(false, msg);
                }
                Map<String, Integer> paramsInfo = moduleRouter.findParamsInfo(target);
                bundle = decodeParams(paramsInfo, pathUri, bundle);
                boolean isParamsValid = preCheck(target, paramsInfo, bundle);
                if (!isParamsValid) {
                    String msg = "can't find required param!";
                    Log.e(TAG, msg);
                    return new RouterResult(false, msg);
                }

                Intent intent = new Intent(context, target);
                intent.putExtras(bundle);
                if (requestCode > 0 && context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, requestCode);
                } else {
                    context.startActivity(intent);
                }
                return new RouterResult(true);
            }
        }
        String msg = "can't find target host module!";
        Log.e(TAG, msg);
        return new RouterResult(false, msg);
    }

    /**
     * @param paramsInfo 目标页面入参的 name 和 type
     * @param uri        uri 对象中可能存在部分参数
     * @param bundle     bundle 对象中也可能存在部分残水
     * @return 最后返回 bundle 对象
     */
    private Bundle decodeParams(Map<String, Integer> paramsInfo, Uri uri, Bundle bundle) {
        Set<String> paramSet = uri.getQueryParameterNames();
        if (paramsInfo == null || paramsInfo.size() == 0) return bundle;
        for (String key : paramSet) {
            Integer type = paramsInfo.get(key);
            if (type == null) {       // 参数找不到时，直接跳过
                continue;
            }
            String value = uri.getQueryParameter(key);
            addToBundle(bundle, type, key, value);
        }
        return bundle;
    }

    /**
     * 将 uri 的属性添加到 bundle 中，Uri 的属性优先级更高
     *
     * @param bundle
     * @param type
     * @param key
     * @param value
     */
    private void addToBundle(Bundle bundle, int type, String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        if (TypeKind.OBJECT.ordinal() == type) {
            bundle.putString(key, value);
        } else if (TypeKind.STRING.ordinal() == type) {
            bundle.putString(key, value);
        } else if (TypeKind.CHAR.ordinal() == type) {
            bundle.putChar(key, value.charAt(0));
        } else if (TypeKind.BYTE.ordinal() == type) {
            bundle.putByte(key, Byte.parseByte(value));
        } else if (TypeKind.SHORT.ordinal() == type) {
            bundle.putShort(key, Short.parseShort(value));
        } else if (TypeKind.INT.ordinal() == type) {
            bundle.putInt(key, Integer.parseInt(value));
        } else if (TypeKind.LONG.ordinal() == type) {
            bundle.putLong(key, Long.parseLong(value));
        } else if (TypeKind.FLOAT.ordinal() == type) {
            bundle.putFloat(key, Float.parseFloat(value));
        } else if (TypeKind.DOUBLE.ordinal() == type) {
            bundle.putDouble(key, Double.parseDouble(value));
        } else if (TypeKind.BOOLEAN.ordinal() == type) {
            bundle.putBoolean(key, Boolean.parseBoolean(value));
        } else if (TypeKind.PARCELABLE.ordinal() == type) {             // uri 不支持该类型
        } else if (TypeKind.SERIALIZABLE.ordinal() == type) {           // uri 不支持该类型
        }
    }
}
