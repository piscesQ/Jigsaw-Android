package com.kore.sample.news

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.kore.sample.interfaceshelf.news.INews

/**
 * @author koreq
 * @date 2021-04-09
 * @description News 模块中的接口实现类
 */

class NewsImpl : INews{
    override fun getNewsFrg(): Fragment {
        return NewsFragment.newInstance("Source_NewsImpl")
    }

    override fun startNewsAct(context: Context) {
        context.startActivity(Intent(context, NewsActivity::class.java))
    }

    override fun getNewsInfo(): String {
        return "This String from News Module"
    }
}


