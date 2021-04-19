package com.kore.live.mock

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.kore.sample.commons.CusToast
import com.kore.sample.interfaceshelf.discover.IDiscover

/**
 * @author koreq
 * @date 2021-04-19
 * @description Mock - Discover 模块中的接口实现类
 */
class DiscoverImpl : IDiscover {
    override fun getDiscoverFrg(): Fragment {
        CusToast.show("Mock - invoke getDiscoverFrg()!")
        return Fragment()
    }

    override fun startDiscoverAct(context: Context) {
        CusToast.show("Mock - invoke startDiscoverAct(context)!")
    }

    override fun getDiscoverInfo(): String {
        return "Mock - This String from News Module"
    }
}


