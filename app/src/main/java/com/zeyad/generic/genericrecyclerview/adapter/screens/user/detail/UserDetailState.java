package com.zeyad.generic.genericrecyclerview.adapter.screens.user.detail;


import com.zeyad.generic.genericrecyclerview.adapter.screens.user.list.User;

import org.parceler.Parcel;
import org.parceler.Transient;

import java.util.List;

/**
 * @author zeyad on 1/25/17.
 */
@Parcel
public class UserDetailState {
    boolean isTwoPane;
    User user;
    @Transient
    List<Repository> repos;

    UserDetailState() {
        user = null;
        repos = null;
        isTwoPane = false;
    }

    private UserDetailState(Builder builder) {
        isTwoPane = builder.isTwoPane;
        user = builder.user;
        repos = builder.repos;
    }

    public static Builder builder() {
        return new Builder();
    }

    boolean isTwoPane() {
        return isTwoPane;
    }

    User getUser() {
        return user;
    }

    List<Repository> getRepos() {
        return repos;
    }

    @Override
    public int hashCode() {
        int result = (isTwoPane ? 1 : 0);
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserDetailState))
            return false;

        UserDetailState that = (UserDetailState) o;

        if (isTwoPane != that.isTwoPane)
            return false;
        return user.equals(that.user);

    }

    public static class Builder {
        List<Repository> repos;
        User user;
        boolean isTwoPane;

        Builder() {
        }

        public Builder setRepos(List<Repository> value) {
            repos = value;
            return this;
        }

        public Builder setIsTwoPane(boolean value) {
            isTwoPane = value;
            return this;
        }

        public Builder setUser(User value) {
            user = value;
            return this;
        }

        public UserDetailState build() {
            return new UserDetailState(this);
        }
    }
}
