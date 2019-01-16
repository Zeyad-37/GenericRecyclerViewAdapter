package com.zeyad.gadapter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class ItemInfo(private val data: @RawValue Any,
                    val layoutId: Int,
                    val id: Long = 0,
                    var isEnabled: Boolean = true) : Parcelable {

    fun <T> getData(): T {
        return data as T
    }

    companion object {
        const val SECTION_HEADER = 1
    }
}
