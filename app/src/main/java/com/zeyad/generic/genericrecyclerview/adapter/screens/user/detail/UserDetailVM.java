package com.zeyad.generic.genericrecyclerview.adapter.screens.user.detail;

import com.zeyad.rxredux.core.BaseEvent;
import com.zeyad.rxredux.core.viewmodel.BaseViewModel;
import com.zeyad.rxredux.core.viewmodel.StateReducer;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * @author zeyad on 1/10/17.
 */
public class UserDetailVM extends BaseViewModel<UserDetailState> {

    //    private IDataService dataUseCase;

    public Flowable<List<Repository>> getRepositories(String userLogin) {
        return Flowable.empty();
        //        return Utils.isNotEmpty(userLogin) ? dataUseCase.<Repository>queryDisk(realm ->
        //                realm.where(Repository.class).equalTo("owner.login", userLogin))
        //                .flatMap(list -> Utils.isNotEmpty(list) ? Flowable.just(list) :
        //                                 dataUseCase.<Repository>getList(new GetRequest.Builder(Repository.class, true)
        //                                .url(String.format(REPOSITORIES, userLogin))
        //                                .build())) :
        //               Flowable.error(new IllegalArgumentException("User name can not be empty"));
    }

    @NotNull
    @Override
    public Function<BaseEvent<?>, Flowable<?>> mapEventsToActions() {
        return baseEvent -> getRepositories(((GetReposEvent) baseEvent).getPayLoad());
    }

    @NotNull
    @Override
    public StateReducer<UserDetailState> stateReducer() {
        return (newResult, baseEvent, currentStateBundle) -> UserDetailState.builder()
                .setRepos((List<Repository>) newResult)
                .setUser(currentStateBundle.getUser())
                .setIsTwoPane(currentStateBundle.isTwoPane())
                .build();
    }
}
