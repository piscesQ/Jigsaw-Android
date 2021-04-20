package com.kore.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.kore.jigsaw.core.Jigsaw
import com.kore.jigsaw.core.router.JRouter
import com.kore.sample.commons.CusToast
import com.kore.sample.interfaceshelf.discover.IDiscover
import com.kore.sample.interfaceshelf.live.ILive
import com.kore.sample.interfaceshelf.me.IMe
import com.kore.sample.interfaceshelf.news.INews
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.simpleName
    private var mFragmentList = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
        initView()
    }

    fun initData() {
        val newsService = Jigsaw.getService(INews::class.java) as INews
        val liveService = Jigsaw.getService(ILive::class.java) as ILive
        val discoverService = Jigsaw.getService(IDiscover::class.java) as IDiscover
        val meService = Jigsaw.getService(IMe::class.java) as IMe

        mFragmentList.add(newsService.getNewsFrg())
        mFragmentList.add(liveService.getLiveFrg("MainActivity"))
        mFragmentList.add(discoverService.getDiscoverFrg())
        mFragmentList.add(meService.getMeFrg())

    }

    fun initView() {
        vp_content.adapter = object :
            FragmentPagerAdapter(supportFragmentManager, 0) {
            override fun getItem(position: Int): Fragment {
                return mFragmentList[position]
            }

            override fun getCount(): Int {
                return mFragmentList.size
            }
        }

        iv_menu.setOnClickListener {
            val openUri = JRouter.get().openUri(this, "jigsaw://app/menu")
            CusToast.show("openUri = $openUri")
        }
    }
}