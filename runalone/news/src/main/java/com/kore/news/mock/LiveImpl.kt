package com.kore.live.mock

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.kore.sample.commons.CusToast
import com.kore.sample.interfaceshelf.live.ILive
import com.kore.sample.interfaceshelf.live.bean.LiveInfo

/**
 * @author koreq
 * @date 2021-04-19
 * @description Mock - Live 模块中的接口实现类
 */
class LiveImpl : ILive {
    override fun getLiveFrg(source: String): Fragment {
        CusToast.show("Mock - invoke getLiveFrg(source)!")
        return Fragment()
    }

    override fun startLiveAct(context: Context) {
        CusToast.show("Mock - invoke startLiveAct(context)!")
    }

    override fun getLiveInfo(): String {
        return "Mock - This String from Live Module"
    }

    override fun openLive(context: Context, info: LiveInfo) {
        CusToast.show("Mock - invoke openLive(context, info)!")
    }
}