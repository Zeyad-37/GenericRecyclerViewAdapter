package com.zeyad.generic.genericrecyclerview.adapter.screens.user.detail;

import com.zeyad.rxredux.core.redux.BaseEvent;
import com.zeyad.rxredux.core.redux.BaseViewModel;
import com.zeyad.rxredux.core.redux.SuccessStateAccumulator;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * @author zeyad on 1/10/17.
 */
public class UserDetailVM extends BaseViewModel<UserDetailState> {

    //    private IDataService dataUseCase;

    @Override
    public void init(SuccessStateAccumulator<UserDetailState> successStateAccumulator,
            UserDetailState initialState, Object... otherDependencies) {
        setSuccessStateAccumulator(successStateAccumulator);
        setInitialState(initialState);
        //        dataUseCase = (IDataService) otherDependencies[0];
    }

    @Override
    public Function<BaseEvent, Flowable<?>> mapEventsToExecutables() {
        return new Function<BaseEvent, Flowable<?>>() {
            @Override
            public Flowable<?> apply(@NonNull BaseEvent baseEvent) throws Exception {
                return getRepositories(((GetReposEvent) baseEvent).getLogin());
            }
        };
        //        return event -> getRepositories(((GetReposEvent) event).getLogin());
    }

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
}
