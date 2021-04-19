package com.kore.sample.discover

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.kore.sample.interfaceshelf.discover.IDiscover

/**
 * @author koreq
 * @date 2021-04-09
 * @description Discover 模块中的接口实现类
 */

class DiscoverImpl : IDiscover {
    override fun getDiscoverFrg(): Fragment {
        return DiscoverFragment.newInstance("Source_DiscoverImpl")
    }

    override fun startDiscoverAct(context: Context) {
        context.startActivity(Intent(context, DiscoverActivity::class.java))
    }

    override fun getDiscoverInfo(): String {
        return "This String from Discover Module"
    }
}


