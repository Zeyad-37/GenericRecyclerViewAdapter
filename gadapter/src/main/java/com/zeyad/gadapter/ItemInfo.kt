package com.zeyad.gadapter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemInfo<T : Parcelable>(val data: T,
                                    val layoutId: Int,
                                    val id: Long = 0,
                                    var isEnabled: Boolean = true) : Parcelable
