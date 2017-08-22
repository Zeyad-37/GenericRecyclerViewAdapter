package com.zeyad.gadapter;

import android.support.annotation.NonNull;

/**
 * @author by zeyad on 20/05/16.
 */
public class ItemInfo {
    public static final int HEADER = 1, FOOTER = 2, LOADING = 3, SECTION_HEADER = 4;
    private final int layoutId;
    private Object data;
    private long id;
    private boolean isEnabled = true;

    public ItemInfo(@NonNull Object data, int layoutId) {
        this.data = data;
        this.layoutId = layoutId;
    }

    public long getId() {
        return id;
    }

    public ItemInfo setId(long id) {
        this.id = id;
        return this;
    }

    @NonNull
    public <T> T getData() {
        return (T) data;
    }

    public void setData(@NonNull Object data) {
        this.data = data;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public ItemInfo setEnabled(boolean enabled) {
        isEnabled = enabled;
        return this;
    }

    @Override
    public int hashCode() {
        int result = layoutId;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (isEnabled ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ItemInfo))
            return false;
        ItemInfo itemInfo = (ItemInfo) o;
        return layoutId == itemInfo.layoutId && id == itemInfo.id && isEnabled == itemInfo.isEnabled && (
                data != null ? data.equals(itemInfo.data) : itemInfo.data == null);
    }
}
