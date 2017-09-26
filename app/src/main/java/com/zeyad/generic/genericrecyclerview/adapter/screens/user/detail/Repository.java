package com.zeyad.generic.genericrecyclerview.adapter.screens.user.detail;


import com.zeyad.generic.genericrecyclerview.adapter.screens.user.list.User;

import org.parceler.Parcel;

/**
 * @author zeyad on 1/25/17.
 */
@Parcel
public class Repository {//extends RealmObject {
    //    @SerializedName("id")
    int id;
    //    @SerializedName("name")
    String name;
    //    @SerializedName("full_name")
    String fullName;
    //    @SerializedName("owner")
    User owner;

    public Repository() {
    }

    public static boolean isEmpty(Repository repository) {
        return repository == null || repository.name == null && repository.fullName == null &&
                repository.owner == null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
