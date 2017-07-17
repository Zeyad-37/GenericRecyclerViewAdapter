package com.zeyad.gadapter;

/**
 * @author by zeyad on 20/05/16.
 */
public class ItemInfo {
    public static final int HEADER = 1, FOOTER = 2, LOADING = 3, SECTION_HEADER = 4;
    private final int layoutId;
    private Object data;
    private long id;
    private boolean isEnabled = true;

    public ItemInfo(Object data, int layoutId) {
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
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

        if (layoutId != itemInfo.layoutId)
            return false;
        if (id != itemInfo.id)
            return false;
        if (isEnabled != itemInfo.isEnabled)
            return false;
        return data != null ? data.equals(itemInfo.data) : itemInfo.data == null;

    }
}
