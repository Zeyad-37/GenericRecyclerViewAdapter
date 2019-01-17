package com.zeyad.gadapter.observables

import com.zeyad.gadapter.GenericViewHolder
import com.zeyad.gadapter.ItemInfo

data class ClickEvent(val position: Int, val itemInfo: ItemInfo, val holder: GenericViewHolder<*>)
