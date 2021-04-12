package com.kore.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kore.jigsaw.core.Jigsaw
import com.kore.sample.interfaceshelf.discover.IDiscover
import com.kore.sample.interfaceshelf.live.ILive
import com.kore.sample.interfaceshelf.me.IMe
import com.kore.sample.interfaceshelf.news.INews
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    fun initView() {
        btn_goto_news.setOnClickListener {
//            startActivity(Intent(this, NewsActivity::class.java))
            var service = Jigsaw.getService(INews::class.java) as INews
            service.startNewsAct(this)
        }
        btn_goto_live.setOnClickListener {
//            startActivity(Intent(this, LiveActivity::class.java))
            var liveService = Jigsaw.getService(ILive::class.java) as ILive
            liveService.startLiveAct(this)
        }
        btn_goto_discover.setOnClickListener {
//            startActivity(Intent(this, DiscoverActivity::class.java))
            var service = Jigsaw.getService(IDiscover::class.java) as IDiscover
            service.startDiscoverAct(this)
        }
        btn_goto_me.setOnClickListener {
//            startActivity(Intent(this, MeActivity::class.java))
            var service = Jigsaw.getService(IMe::class.java) as IMe
            service.startMeAct(this)
        }

    }
}