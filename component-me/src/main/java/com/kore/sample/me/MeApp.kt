package com.kore.sample.me

import android.util.Log
import com.kore.jigsaw.anno.ModuleApp
import com.kore.jigsaw.core.Jigsaw
import com.kore.sample.commons.base.BaseApp
import com.kore.sample.interfaceshelf.me.IMe

/**
 * @author koreq
 * @date 2021-04-10
 * @description Me 模块的 Application，该类的实例会在 gradle 插件中存入 [com.kore.jigsaw.core.Jigsaw.mModuleAppList]
 * 最后会将所有 module 的 Application 实例的方法注入到主应用的 Application 中的对应方法中
 */

private const val TAG = "BaseApp-Me"

@ModuleApp(priority = 40)
class MeApp : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MeApp onCreate")
        Jigsaw.addRelation(IMe::class.java, MeImpl::class.java)
    }
}