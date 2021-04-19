package com.kore.live.mock

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.kore.sample.commons.CusToast
import com.kore.sample.interfaceshelf.me.IMe

/**
 * @author koreq
 * @date 2021-04-19
 * @description Mock - Me 模块中的接口实现类
 */
class MeImpl : IMe {
    override fun getMeFrg(): Fragment {
        CusToast.show("Mock - invoke getMeFrg()!")
        return Fragment()
    }

    override fun startMeAct(context: Context) {
        CusToast.show("Mock - invoke startMeAct(context)!")
    }

    override fun getMeInfo(): String {
        return "Mock - This String from Me Module"
    }
}