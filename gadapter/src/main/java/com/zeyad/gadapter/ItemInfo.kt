package com.zeyad.gadapter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class ItemInfo<T>(val data: @RawValue T,
                       val layoutId: Int,
                       val id: Long = 0,
                       var isEnabled: Boolean = true) : Parcelable
