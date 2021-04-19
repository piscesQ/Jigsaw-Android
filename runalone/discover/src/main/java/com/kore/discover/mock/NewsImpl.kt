package com.kore.live.mock

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.kore.sample.commons.CusToast
import com.kore.sample.interfaceshelf.news.INews

/**
 * @author koreq
 * @date 2021-04-19
 * @description Mock - News 模块中的接口实现类
 */
class NewsImpl : INews{
    override fun getNewsFrg(): Fragment {
        CusToast.show("Mock - invoke getNewsFrg()!")
        return Fragment()
    }

    override fun startNewsAct(context: Context) {
        CusToast.show("Mock - invoke startNewsAct(context)!")
    }

    override fun getNewsInfo(): String {
        return "Mock - This String from News Module"
    }
}


