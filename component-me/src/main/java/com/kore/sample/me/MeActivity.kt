package com.kore.sample.me

import android.os.Bundle
import com.kore.sample.commons.CusToast
import com.kore.sample.commons.base.BaseActivity
import kotlinx.android.synthetic.main.me_activity_me.*

class MeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.me_activity_me)

        initView()
    }

    private fun initView() {
        iv_fruit.setOnClickListener {
            CusToast.show("我是橙子！")
        }
    }
}