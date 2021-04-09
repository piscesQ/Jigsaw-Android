package com.kore.sample.interfaceshelf.live

import android.content.Context
import androidx.fragment.app.Fragment
import com.kore.sample.interfaceshelf.live.bean.LiveInfo

/**
 * Live 模块对外提供接口
 */
interface ILive {

    fun getLiveFrg(source:String): Fragment;

    fun startLiveAct(context: Context)

    fun getLiveInfo(): String

    fun openLive(context: Context, info: LiveInfo)
}
