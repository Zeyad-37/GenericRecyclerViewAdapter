package com.zeyad.gadapter.Observables;

import com.zeyad.gadapter.GenericViewHolder;
import com.zeyad.gadapter.ItemInfo;

/**
 * @author ZIaDo on 12/8/17.
 */
public final class ClickEvent {

    private final int position;
    private final ItemInfo itemInfo;
    private final GenericViewHolder holder;


    public ClickEvent(int position, ItemInfo itemInfo, GenericViewHolder holder) {
        this.position = position;
        this.itemInfo = itemInfo;
        this.holder = holder;
    }

    public int getPosition() {
        return position;
    }

    public ItemInfo getItemInfo() {
        return itemInfo;
    }

    public GenericViewHolder getHolder() {
        return holder;
    }
}
