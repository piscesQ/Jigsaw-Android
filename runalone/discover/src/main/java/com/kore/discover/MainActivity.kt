package com.kore.discover

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.kore.jigsaw.core.Jigsaw
import com.kore.jigsaw.core.router.JRouter
import com.kore.sample.interfaceshelf.discover.IDiscover
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
        val discoverService = Jigsaw.getService(IDiscover::class.java) as IDiscover

        mFragmentList.add(discoverService.getDiscoverFrg())

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

        tv_act.setOnClickListener {
            JRouter.get().openUri(this, "jigsaw://discover/discover_home")
        }
    }
}