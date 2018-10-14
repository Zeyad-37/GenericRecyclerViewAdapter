package com.zeyad.gadapter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author by zeyad on 20/05/16.
 */
public class ItemInfo implements Parcelable {

    public static final int HEADER = 1;
    public static final int FOOTER = 2;
    public static final int LOADING = 3;
    public static final int SECTION_HEADER = 4;
    private final int layoutId;
    private Object data;
    private long id;
    private boolean isEnabled = true;
    public static final Creator<ItemInfo> CREATOR = new Creator<ItemInfo>() {
        @Override
        public ItemInfo createFromParcel(Parcel in) {
            return new ItemInfo(in);
        }

        @Override
        public ItemInfo[] newArray(int size) {
            return new ItemInfo[size];
        }
    };

    public ItemInfo(Object data, int layoutId) {
        this.data = data;
        this.layoutId = layoutId;
    }

    protected ItemInfo(Parcel in) {
        layoutId = in.readInt();
        id = in.readLong();
        isEnabled = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(layoutId);
        dest.writeLong(id);
        dest.writeByte((byte) (isEnabled ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getId() {
        return id;
    }

    public ItemInfo setId(long id) {
        this.id = id;
        return this;
    }

    public <T> T getData() {
        return (T) data;
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
        return layoutId == itemInfo.layoutId && id == itemInfo.id && isEnabled == itemInfo.isEnabled && (
                data != null ? data.equals(itemInfo.data) : itemInfo.data == null);
    }
}
