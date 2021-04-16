package com.kore.sample.news

import android.os.Bundle
import com.kore.jigsaw.anno.router.Route
import com.kore.sample.commons.CusToast
import com.kore.sample.commons.base.BaseActivity
import kotlinx.android.synthetic.main.news_activity_news.*

@Route(path = "/news_home")
class NewsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_activity_news)

        initView()
    }

    private fun initView() {
        iv_fruit.setOnClickListener {
            CusToast.show("我是榴莲！")
        }
    }
}