package com.kore.sample.commons.base

import android.app.Application
import androidx.annotation.CallSuper

open class BaseApp : Application() {
    @CallSuper
    override fun onCreate() {
        super.onCreate()
        INSTANCE = applicationContext as Application
    }

    companion object {
        @JvmField
        var INSTANCE: Application? = null
    }
}