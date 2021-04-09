package com.kore.sample.interfaceshelf.discover

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * Discover 模块对外提供接口
 */
interface IDiscover {

    fun getDiscoverFrg(): Fragment;

    fun startDiscoverAct(context: Context)

    fun getDiscoverInfo(): String

}
