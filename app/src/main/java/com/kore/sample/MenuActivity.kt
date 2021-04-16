package com.kore.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kore.jigsaw.anno.router.Route
import com.kore.jigsaw.core.router.JRouter
import com.kore.sample.bean.Computer
import com.kore.sample.bean.Person
import kotlinx.android.synthetic.main.act_menu.*
import java.io.File

@Route(path = "/menu")
class MenuActivity : AppCompatActivity() {
    private val TAG = MenuActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_menu)

        initView()
    }

    private fun initView() {
        btn_goto_news.setOnClickListener {
//            startActivity(Intent(this, NewsActivity::class.java))

//            var service = Jigsaw.getService(INews::class.java) as INews
//            service.startNewsAct(this)

            var path = "jigsaw://news/news_home"
            var result = JRouter.get().openUri(this, path)
            Log.d(TAG, "JRouter news result = $result")
        }

        btn_goto_live.setOnClickListener {
//            startActivity(Intent(this, LiveActivity::class.java))

//            var liveService = Jigsaw.getService(ILive::class.java) as ILive
//            liveService.startLiveAct(this)

            var path = "jigsaw://live/live_home"
            var result = JRouter.get().openUri(this, path)
            Log.d(TAG, "JRouter live result = $result")
        }

        btn_goto_discover.setOnClickListener {
//            startActivity(Intent(this, DiscoverActivity::class.java))

//            var service = Jigsaw.getService(IDiscover::class.java) as IDiscover
//            service.startDiscoverAct(this)

            var path = "jigsaw://discover/discover_home"
            var result = JRouter.get().openUri(this, path)
            Log.d(TAG, "JRouter discover result = $result")
        }

        btn_goto_me.setOnClickListener {
//            startActivity(Intent(this, MeActivity::class.java))

//            var service = Jigsaw.getService(IMe::class.java) as IMe
//            service.startMeAct(this)

            var path = "jigsaw://me/me_home"
            var result = JRouter.get().openUri(this, path)
            Log.d(TAG, "JRouter me result = $result")
        }

        btn_goto_detail_no_params.setOnClickListener {
            val path = "jigsaw://app/detail"
            var result = JRouter.get().openUri(this, path)
            Log.d(TAG, "JRouter detail result = $result")
        }

        btn_goto_detail.setOnClickListener {
            var bundle = Bundle().apply {
                putDouble("price", 99.99)
                putChar("letter", 'Z')
                putSerializable("music", File("/User/Desktop/999.mp3"))
                putParcelable("computer", Computer("9", "Macbook", "Apple"))
                putParcelable("person", Person("用户名9", "密码9"))
            }
            val path = "jigsaw://app/detail?" +
                    "type=999" +
                    "&key=梧桐" +
                    "&flag=true" +
                    "&price=999.99" +
                    "&letter=Y" +
                    "&user={\"name\":\"芦苇\",\"age\":99}"
            var result = JRouter.get().openUri(this, path, bundle)
            Log.d(TAG, "JRouter detail result = $result")
        }

        btn_goto_about.setOnClickListener {
            var path = "jigsaw://app/about?mSource=菜单页"
            var result = JRouter.get().openUri(this, path)
            Log.d(TAG, "JRouter about result = $result")
        }
    }
}