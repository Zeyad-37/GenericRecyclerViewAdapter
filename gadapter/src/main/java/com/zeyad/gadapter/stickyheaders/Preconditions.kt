package com.zeyad.gadapter.stickyheaders

import android.view.View
import android.widget.FrameLayout

internal object Preconditions {

    fun <T> checkNotNull(item: T?, message: String): T {
        if (item == null) {
            throw NullPointerException(message)
        }
        return item
    }

    fun validateParentView(recyclerView: View) {
        recyclerView.parent as View as? FrameLayout
                ?: throw IllegalArgumentException("RecyclerView parent must be either a FrameLayout or CoordinatorLayout")
    }
}
