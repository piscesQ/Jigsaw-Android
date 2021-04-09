package com.kore.sample.live

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.kore.sample.interfaceshelf.live.ILive
import com.kore.sample.interfaceshelf.live.bean.LiveInfo

/**
 * @author koreq
 * @date 2021-04-09
 * @description Live 模块中的接口实现类
 */
class LiveImpl : ILive {
    override fun getLiveFrg(source: String): Fragment {
        return LiveFragment.newInstance(source)
    }

    override fun startLiveAct(context: Context) {
        context.startActivity(Intent(context, LiveActivity::class.java))
    }

    override fun getLiveInfo(): String {
        return "This String from Live Module"
    }

    override fun openLive(context: Context, info: LiveInfo) {
        LiveDetailActivity.launchAct(context, info.title, info.content)
    }
}