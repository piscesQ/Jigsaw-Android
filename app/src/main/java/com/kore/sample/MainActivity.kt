package com.kore.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        }
        btn_goto_live.setOnClickListener {
//            startActivity(Intent(this, LiveActivity::class.java))
        }
        btn_goto_discover.setOnClickListener {
//            startActivity(Intent(this, DiscoverActivity::class.java))
        }
        btn_goto_me.setOnClickListener {
//            startActivity(Intent(this, MeActivity::class.java))
        }

    }
}