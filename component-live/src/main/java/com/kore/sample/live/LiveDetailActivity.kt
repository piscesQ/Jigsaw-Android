package com.kore.sample.live

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.live_act_detail.*

private const val KEY_TITLE = "KEY_TITLE"
private const val KEY_CONTENT = "KEY_CONTENT"

class LiveDetailActivity : AppCompatActivity() {
    lateinit var mTitle: String
    lateinit var mContent: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_act_detail)
        tv_detail_content.text = mTitle
        tv_detail_content.text = mContent
    }

    companion object {
        @JvmStatic
        fun launchAct(context: Context, title: String, content: String) {
            val intent = Intent(context, LiveDetailActivity::class.java)
            intent.apply {
                putExtra(KEY_TITLE, title)
                putExtra(KEY_CONTENT, content)
            }
            context.startActivity(intent)
        }
    }
}