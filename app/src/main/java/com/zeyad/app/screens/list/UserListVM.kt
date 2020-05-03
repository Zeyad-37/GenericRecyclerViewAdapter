package com.zeyad.app.screens.list

import androidx.recyclerview.widget.DiffUtil
import com.zeyad.app.R
import com.zeyad.app.screens.User
import com.zeyad.app.screens.UserDiffCallBack
import com.zeyad.app.utils.Constants.URLS.USER
import com.zeyad.app.utils.Constants.URLS.USERS
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.StringMessage
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.db.RealmQueryProvider
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.realm.Realm
import io.realm.RealmQuery

class UserListVM(private val dataUseCase: IDataService) : BaseViewModel<UserListState, UserListEffect>() {
    override var disposable: CompositeDisposable = CompositeDisposable()

    override fun errorMessageFactory(throwable: Throwable, event: BaseEvent<*>) =
            StringMessage(throwable.localizedMessage)

    override fun mapEventsToActions(event: BaseEvent<*>): Flowable<*> {
        val userListEvent = event as UserListEvents<*>
        return when (userListEvent) {
            is GetPaginatedUsersEvent -> getUsers(userListEvent.getPayLoad())
            is DeleteUsersEvent -> deleteCollection(userListEvent.getPayLoad())
            is SearchUsersEvent -> search(userListEvent.getPayLoad())
        }
    }

    override fun reducer(newResult: Any, event: BaseEvent<*>, currentStateBundle: UserListState): UserListState {
        val currentItemInfo = currentStateBundle.list.toMutableList()
        return when (currentStateBundle) {
            is EmptyState -> when (newResult) {
                is List<*> -> {
                    val pair = Flowable.fromIterable(newResult as List<User>)
                            .map { ItemInfo(it, R.layout.user_item_layout, it.id) }
                            .toList()
                            .map { it as MutableList<ItemInfo<*>> }
                            .toFlowable()
                            .calculateDiff(currentItemInfo)
                    GetState(pair.first, pair.first[pair.first.size - 1].id, pair.second)
                }
                else -> throw IllegalStateException("Can not reduce GetState with this result: $newResult!")
            }
            is GetState -> when (newResult) {
                is List<*> -> {
                    val pair = Flowable.fromIterable(newResult as List<User>)
                            .map { ItemInfo(it, R.layout.user_item_layout, it.id) }
                            .toList()
                            .map {
                                val list = currentStateBundle.list.toMutableList()
                                list.addAll(it)
                                list.toSet().toMutableList()
                            }.toFlowable()
                            .calculateDiff(currentItemInfo)
                    GetState(pair.first, pair.first[pair.first.size - 1].id, pair.second)
                }
                else -> throw IllegalStateException("Can not reduce GetState with this result: $newResult!")
            }
        }
    }

    private fun Flowable<MutableList<ItemInfo<*>>>.calculateDiff(initialList: MutableList<ItemInfo<*>>)
            : Pair<MutableList<ItemInfo<*>>, DiffUtil.DiffResult> =
            scan(Pair(initialList, DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))))
            { pair1, next ->
                Pair(next, DiffUtil.calculateDiff(UserDiffCallBack(pair1.first, next)))
            }.skip(1)
                    .blockingFirst()

    private fun getUsers(lastId: Long): Flowable<List<User>> {
//        return if (lastId == 0L)
//            dataUseCase.getListOffLineFirst(GetRequest.Builder(User::class.java, true)
//                    .url(String.format(USERS, lastId))
//                    .build())
//        else
        return dataUseCase.getList(GetRequest.Builder(User::class.java, true)
                .url(String.format(USERS, lastId)).build())
    }

    private fun search(query: String): Flowable<List<User>> {
        return dataUseCase
                .queryDisk(object : RealmQueryProvider<User> {
                    override fun create(realm: Realm): RealmQuery<User> =
                            realm.where(User::class.java).beginsWith(User.LOGIN, query)
                })
                .zipWith(dataUseCase.getObject<User>(GetRequest.Builder(User::class.java, false)
                        .url(String.format(USER, query)).build())
                        .onErrorReturnItem(User())
                        .filter { user -> user.id != 0L }
                        .map { mutableListOf(it) },
                        BiFunction<List<User>, MutableList<User>, List<User>>
                        { singleton, users ->
                            users.addAll(singleton)
                            users.asSequence().toSet().toList()
                        })
    }

    private fun deleteCollection(selectedItemsIds: List<String>): Flowable<List<String>> {
        return dataUseCase.deleteCollectionByIds<Any>(PostRequest.Builder(User::class.java, true)
                .payLoad(selectedItemsIds)
                .idColumnName(User.LOGIN, String::class.java).cache()
                .build())
                .map { selectedItemsIds }
    }
}
