package com.kore.sample.discover

import android.os.Bundle
import com.kore.jigsaw.anno.router.Route
import com.kore.sample.commons.CusToast
import com.kore.sample.commons.base.BaseActivity
import kotlinx.android.synthetic.main.discover_act_discover.*

@Route(path = "/discover_home")
class DiscoverActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_act_discover)

        initView()
    }

    private fun initView() {
        iv_fruit.setOnClickListener {
            CusToast.show("我是蓝莓！")
        }
    }
}