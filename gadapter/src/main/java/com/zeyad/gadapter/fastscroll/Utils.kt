package com.zeyad.gadapter.fastscroll

import android.graphics.drawable.Drawable
import android.view.View

fun getViewRawY(view: View): Float {
    val location = IntArray(2)
    location[0] = 0
    location[1] = view.y.toInt()
    (view.parent as View).getLocationInWindow(location)
    return location[1].toFloat()
}

fun getViewRawX(view: View): Float {
    val location = IntArray(2)
    location[0] = view.x.toInt()
    location[1] = 0
    (view.parent as View).getLocationInWindow(location)
    return location[0].toFloat()
}

fun getValueInRange(min: Float, max: Float, value: Float): Float {
    val minimum = Math.max(min, value)
    return Math.min(minimum, max)
}

fun setBackground(view: View, drawable: Drawable) {
    view.background = drawable
}
