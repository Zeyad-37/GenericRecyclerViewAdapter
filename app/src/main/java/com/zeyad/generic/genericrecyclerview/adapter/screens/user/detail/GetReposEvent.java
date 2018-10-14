package com.zeyad.generic.genericrecyclerview.adapter.screens.user.detail;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.zeyad.rxredux.core.BaseEvent;

/**
 * @author by ZIaDo on 4/22/17.
 */
@SuppressLint("ParcelCreator")
class GetReposEvent implements BaseEvent<String> {
    private final String login;

    GetReposEvent(String login) {
        this.login = login;
    }

    @Override
    public String getPayLoad() {
        return login;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
