package com.zeyad.gadapter

import android.os.Looper

import io.reactivex.Observer

fun Observer<*>.checkMainThread(): Boolean {
    if (Looper.myLooper() != Looper.getMainLooper()) {
        onError(IllegalStateException(
                "Expected to be called on the main thread but was " + Thread.currentThread().name))
        return false
    }
    return true
}
