package com.zeyad.gadapter.stickyheaders.exposed

import androidx.recyclerview.widget.RecyclerView

import com.zeyad.gadapter.ItemInfo

interface StickyHeaderHandler {

    /**
     * @return The dataset supplied to the [RecyclerView.Adapter]
     */
    val adapterData: List<ItemInfo<*>>
}
