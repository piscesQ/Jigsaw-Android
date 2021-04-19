package com.kore.live

import com.kore.jigsaw.anno.MainApp
import com.kore.jigsaw.core.Jigsaw
import com.kore.live.mock.DiscoverImpl
import com.kore.live.mock.LiveImpl
import com.kore.live.mock.MeImpl
import com.kore.live.mock.NewsImpl
import com.kore.sample.commons.base.BaseApp
import com.kore.sample.interfaceshelf.discover.IDiscover
import com.kore.sample.interfaceshelf.live.ILive
import com.kore.sample.interfaceshelf.me.IMe
import com.kore.sample.interfaceshelf.news.INews

@MainApp
class MainApp : BaseApp() {
    override fun onCreate() {
        super.onCreate()

        Jigsaw.addRelation(IDiscover::class.java, DiscoverImpl::class.java)
        Jigsaw.addRelation(ILive::class.java, LiveImpl::class.java)
        Jigsaw.addRelation(IMe::class.java, MeImpl::class.java)
        Jigsaw.addRelation(INews::class.java, NewsImpl::class.java)
    }
}
