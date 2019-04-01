package com.zeyad.app.screens.detail

import android.content.Intent
import android.os.Parcelable
import com.zeyad.app.screens.User
import com.zeyad.gadapter.ItemInfo
import kotlinx.android.parcel.Parcelize

sealed class UserDetailState : Parcelable

@Parcelize
data class IntentBundleState(val isTwoPane: Boolean = false,
                             val user: User = User()) : UserDetailState()

@Parcelize
data class FullDetailState(val isTwoPane: Boolean = false,
                           val user: User = User(),
                           val repos: List<ItemInfo<*>> = emptyList()) : UserDetailState()

@Parcelize
data class NavigateFromDetail(val intent: Intent, val shouldFinish: Boolean) : UserDetailState()
