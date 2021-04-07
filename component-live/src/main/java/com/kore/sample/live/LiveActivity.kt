package com.kore.sample.live

import android.os.Bundle
import com.kore.sample.commons.CusToast
import com.kore.sample.commons.base.BaseActivity
import kotlinx.android.synthetic.main.live_frg_live.*

class LiveActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_activity_live)

        initView()
    }

    private fun initView() {
        iv_fruit.setOnClickListener {
            CusToast.show("我是樱桃！")
        }
    }
}