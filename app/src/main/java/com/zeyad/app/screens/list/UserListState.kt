package com.zeyad.app.screens.list

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.zeyad.app.screens.UserDiffCallBack
import com.zeyad.gadapter.ItemInfo
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

sealed class UserListState : Parcelable {
    abstract val list: List<ItemInfo<*>>
    abstract val lastId: Long
    abstract val callback: DiffUtil.DiffResult
}

@Parcelize
data class EmptyState(override val list: List<ItemInfo<*>> = emptyList(),
                      override val lastId: Long = 1
) : UserListState(), Parcelable {
    @IgnoredOnParcel
    override var callback: @RawValue DiffUtil.DiffResult =
            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
}

@Parcelize
data class GetState(override val list: List<ItemInfo<*>> = emptyList(),
                    override val lastId: Long = 1
) : UserListState(), Parcelable {
    @IgnoredOnParcel
    override var callback: @RawValue DiffUtil.DiffResult =
            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))

    constructor(list: List<ItemInfo<*>> = emptyList(), lastId: Long = 1,
                callback: @RawValue DiffUtil.DiffResult =
                        DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
    ) : this(list, lastId) {
        this.callback = callback
    }
}
