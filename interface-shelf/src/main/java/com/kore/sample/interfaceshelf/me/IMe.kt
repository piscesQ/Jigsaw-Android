package com.kore.sample.interfaceshelf.me

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * Me 模块对外提供接口
 */
interface IMe {

    fun getMeFrg(): Fragment;

    fun startMeAct(context: Context)

    fun getMeInfo(): String

}
