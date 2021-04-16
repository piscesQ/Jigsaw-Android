package com.kore.sample.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author koreq
 * @date 2021-04-19
 * @description
 */
@Parcelize
data class Computer(val id: String, val model: String, val brand: String) : Parcelable{
    override fun toString(): String {
        return "Computer(id='$id', model='$model', brand='$brand')"
    }
}
