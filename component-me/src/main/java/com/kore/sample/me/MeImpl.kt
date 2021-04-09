package com.kore.sample.me

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.kore.sample.interfaceshelf.me.IMe

/**
 * @author koreq
 * @date 2021-04-09
 * @description Me 模块中的接口实现类
 */
class MeImpl : IMe {
    override fun getMeFrg(): Fragment {
        return MeFragment.newInstance("Source_MeImpl")
    }

    override fun startMeAct(context: Context) {
        context.startActivity(Intent(context, MeActivity::class.java))
    }

    override fun getMeInfo(): String {
        return "This String from Me Module"
    }
}