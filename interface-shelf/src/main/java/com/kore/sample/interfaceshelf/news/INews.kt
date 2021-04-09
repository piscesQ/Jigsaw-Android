package com.kore.sample.interfaceshelf.news

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * News 模块对外提供接口
 */
interface INews {

    fun getNewsFrg(): Fragment;

    fun startNewsAct(context: Context)

    fun getNewsInfo(): String

}
