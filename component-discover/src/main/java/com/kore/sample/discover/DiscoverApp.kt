package com.kore.sample.discover

import android.util.Log
import com.kore.sample.commons.base.BaseApp

/**
 * @author koreq
 * @date 2021-04-10
 * @description Discover 模块的 Application，该类的实例会在 gradle 插件中存入 [com.kore.jigsaw.core.Jigsaw.mModuleAppList]
 * 最后会将所有 module 的 Application 实例的方法注入到主应用的 Application 中的对应方法中
 */

private const val TAG = "BaseApp-Discover"

class DiscoverApp : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "DiscoverApp onCreate")
    }
}