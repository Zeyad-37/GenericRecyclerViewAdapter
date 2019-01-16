package com.zeyad.gadapter.utils

import android.os.Build

fun hasLollipop(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

fun hasM(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

