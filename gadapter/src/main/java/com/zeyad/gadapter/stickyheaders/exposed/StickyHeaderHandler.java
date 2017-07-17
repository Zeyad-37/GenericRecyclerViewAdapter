package com.zeyad.gadapter.stickyheaders.exposed;

import android.support.v7.widget.RecyclerView;

import com.zeyad.gadapter.ItemInfo;

import java.util.List;

public interface StickyHeaderHandler {

    /**
     * @return The dataset supplied to the {@link RecyclerView.Adapter}
     */
    List<ItemInfo> getAdapterData();
}
