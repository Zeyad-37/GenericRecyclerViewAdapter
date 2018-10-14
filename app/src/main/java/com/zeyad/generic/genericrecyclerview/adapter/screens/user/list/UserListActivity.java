package com.zeyad.generic.genericrecyclerview.adapter.screens.user.list;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zeyad.gadapter.GenericRecyclerViewAdapter;
import com.zeyad.gadapter.ItemInfo;
import com.zeyad.gadapter.OnStartDragListener;
import com.zeyad.gadapter.SimpleItemTouchHelperCallback;
import com.zeyad.gadapter.fastscroll.FastScroller;
import com.zeyad.gadapter.stickyheaders.StickyGridLayoutManager;
import com.zeyad.generic.genericrecyclerview.R;
import com.zeyad.generic.genericrecyclerview.adapter.screens.BaseActivity;
import com.zeyad.generic.genericrecyclerview.adapter.screens.user.detail.UserDetailActivity;
import com.zeyad.generic.genericrecyclerview.adapter.screens.user.detail.UserDetailFragment;
import com.zeyad.generic.genericrecyclerview.adapter.screens.user.detail.UserDetailState;
import com.zeyad.generic.genericrecyclerview.adapter.screens.user.list.events.GetPaginatedUsersEvent;
import com.zeyad.generic.genericrecyclerview.adapter.screens.user.list.viewHolders.EmptyGenericViewHolder;
import com.zeyad.generic.genericrecyclerview.adapter.screens.user.list.viewHolders.SectionHeaderGenericViewHolder;
import com.zeyad.generic.genericrecyclerview.adapter.screens.user.list.viewHolders.UserGenericViewHolder;
import com.zeyad.generic.genericrecyclerview.adapter.screens.utils.Utils;
import com.zeyad.rxredux.core.BaseEvent;
import com.zeyad.rxredux.core.view.ErrorMessageFactory;
import com.zeyad.usecases.api.DataServiceFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Single;

import static com.zeyad.gadapter.ItemInfo.SECTION_HEADER;

/**
 * An activity representing a list of Repos. This activity has different presentations for handset
 * and tablet-size devices. On handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing item details. On tablets, the activity presents
 * the list of items and item details side-by-side using two vertical panes.
 */
public class UserListActivity extends BaseActivity<UserListState, UserListVM> implements OnStartDragListener, ActionMode.Callback {

    @BindView(R.id.imageView_avatar)
    public ImageView imageViewAvatar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;

    @BindView(R.id.user_list)
    RecyclerView userRecycler;

    @BindView(R.id.fastscroll)
    FastScroller fastScroller;

    private ItemTouchHelper itemTouchHelper;
    private GenericRecyclerViewAdapter usersAdapter;
    private ActionMode actionMode;
    private String currentFragTag;
    private boolean twoPane;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    @Override
    public ErrorMessageFactory errorMessageFactory() {
        return Throwable::getLocalizedMessage;
    }

    @Override
    public void initialize() {
        viewModel = ViewModelProviders.of(this).get(UserListVM.class);
        viewModel.init(getUserListStateSuccessStateAccumulator(), viewState, DataServiceFactory.getInstance());
        if (viewState == null) {
            events = Single.<BaseEvent>just(new GetPaginatedUsersEvent(0))
                    //                    .doOnSuccess(event -> Log.d("GetPaginatedUsersEvent", "fired!"))
                    .toObservable();
        }
        //        rxEventBus.toFlowable()
        //                  .compose(bindToLifecycle())
        //                  .flatMap(new Function<Object, Flowable<?>>() {
        //                      @Override
        //                      public Flowable<?> apply(@io.reactivex.annotations.NonNull Object stream) throws Exception {
        //                          return events.mergeWith((Observable<BaseEvent>) stream)
        //                                       .toFlowable(BackpressureStrategy.BUFFER)
        //                                       .compose(uiModelsTransformer);
        //                      }
        //                  })
        //                  .subscribe((Consumer<? super Object>) new UISubscriber<>(this, errorMessageFactory()));
    }

    //    @Override
    //    protected void onResume() {
    //        super.onResume();
    //        viewModel.getUser()
    //                 .compose(bindToLifecycle())
    //                 .doOnCancel(() -> Log.d("Test", "Cancelled"))
    //                 .subscribe(user -> Log.d("Test", user.toString()),
    //                         throwable -> {
    //                         });
    //    }

    @Override
    public void setupUI(boolean isNew) {
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        setupRecyclerView();
        twoPane = findViewById(R.id.user_detail_container) != null;
    }

    @NonNull
    private SuccessStateAccumulator<UserListState> getUserListStateSuccessStateAccumulator() {
        return (newResult, event, s1) -> {
            List resultList = (List) newResult;
            List<User> users = new ArrayList<>();
            //            List<User> users = currentStateBundle == null ? new ArrayList<>() :
            //                               currentStateBundle.getUsers();
            List<User> searchList = new ArrayList<>();
            switch (event) {
                case "GetPaginatedUsersEvent":
                    users.addAll(resultList);
                    break;
                case "SearchUsersEvent":
                    searchList.clear();
                    searchList.addAll(resultList);
                    break;
                case "DeleteUsersEvent":
                    users = Observable.fromIterable(users)
                            //                                      .filter(user -> !resultList.contains((long) user.getId()))
                            .distinct()
                            .toList()
                            .blockingGet();
                    break;
                default:
                    break;
            }
            int lastId = users.get(users.size() - 1).getId();
            users = new ArrayList<>(new HashSet<>(users));
            //            Collections.sort(users, (user1, user2) ->
            //                    String.valueOf(user1.getId()).compareTo(String.valueOf(user2.getId())));
            return UserListState.builder().users(users).searchList(searchList).lastId(lastId).build();
        };
    }

    @Override
    public void renderSuccessState(UserListState state) {
        viewState = state;
        List<User> users = viewState.getUsers();
        List<User> searchList = viewState.getSearchList();
        if (Utils.isNotEmpty(searchList)) {
            usersAdapter.animateTo(Observable.fromIterable(searchList)
                    .map(user -> new ItemInfo(user, R.layout.user_item_layout).setId(user.getId()))
                    .toList(users.size())
                    .blockingGet());
        } else if (Utils.isNotEmpty(users)) {
            usersAdapter.animateTo(Observable.fromIterable(users)
                    .map(user -> new ItemInfo(user, R.layout.user_item_layout).setId(user.getId()))
                    .toList(users.size()).blockingGet());
            usersAdapter.addSectionHeader(0, "1st Section");
            usersAdapter.addSectionHeader(4, "2nd Section");
        }
    }

    @Override
    public void toggleViews(boolean toggle) {
        loaderLayout.bringToFront();
        loaderLayout.setVisibility(toggle ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, userRecycler, Snackbar.LENGTH_LONG);
    }

    private void setupRecyclerView() {
        usersAdapter = new GenericRecyclerViewAdapter(
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), new ArrayList<>()) {
            @Override
            public GenericViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case SECTION_HEADER:
                        return new SectionHeaderGenericViewHolder(getLayoutInflater().inflate(R.layout.section_header_layout,
                                parent, false));
                    case R.layout.empty_view:
                        return new EmptyGenericViewHolder(getLayoutInflater().inflate(R.layout.empty_view,
                                parent, false));
                    case R.layout.user_item_layout:
                        return new UserGenericViewHolder(getLayoutInflater().inflate(R.layout.user_item_layout,
                                parent, false));
                    default:
                        return null;
                }
            }
        };
        //        usersAdapter.setSectionTitleProvider(i -> "Section " + (i + 1));
        usersAdapter.setAreItemsExpandable(true);
        usersAdapter.setAreItemsClickable(true);
        usersAdapter.setOnItemClickListener((position, itemInfo, holder) -> {
            if (actionMode != null) {
                toggleSelection(position);
            } else if (itemInfo.getData() instanceof User) {
                User userModel = itemInfo.getData();
                UserDetailState userDetailState = UserDetailState.builder()
                        .setUser(userModel)
                        .setIsTwoPane(twoPane)
                        .build();
                Pair<View, String> pair = null;
                Pair<View, String> secondPair = null;
                if (Utils.hasLollipop()) {
                    UserGenericViewHolder userViewHolder = (UserGenericViewHolder) holder;
                    ImageView avatar = userViewHolder.getAvatar();
                    //                    pair = Pair.create(avatar, avatar.getTransitionName());
                    TextView textViewTitle = userViewHolder.getTextViewTitle();
                    //                    secondPair = Pair.create(textViewTitle, textViewTitle.getTransitionName());
                }
                if (twoPane) {
                    List<Pair<View, String>> pairs = new ArrayList<>();
                    pairs.add(pair);
                    pairs.add(secondPair);
                    if (Utils.isNotEmpty(currentFragTag)) {
                        removeFragment(currentFragTag);
                    }
                    UserDetailFragment orderDetailFragment = UserDetailFragment.newInstance(userDetailState);
                    currentFragTag = orderDetailFragment.getClass().getSimpleName() + userModel.getId();
//                    addFragment(R.id.user_detail_container, orderDetailFragment, currentFragTag, pairs);
                } else {
                    if (Utils.hasLollipop()) {
//                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
//                                pair, secondPair);
//                        navigator.navigateTo(this, UserDetailActivity.getCallingIntent(this,
//                                userDetailState), options);
                    } else {
                        navigator.navigateTo(this, UserDetailActivity.getCallingIntent(this, userDetailState));
                    }
                }
            }
        });
        //        usersAdapter.setOnItemLongClickListener((position, itemInfo, holder) -> {
        //                    if (usersAdapter.isSelectionAllowed()) {
        //                        actionMode = startSupportActionMode(UserListActivity.this);
        //                        toggleSelection(position);
        //                    }
        //                    return true;
        //                });
        //        usersAdapter.setOnItemSwipeListener(itemInfo -> {
        //            events = events.mergeWith(Observable.defer(() ->
        //                    Observable.just(new DeleteUsersEvent(Collections.singletonList(((User) itemInfo.getData()).getLogin())));
        //                              .doOnEach(notification -> Log.d("DeleteEvent", "fired!"))));
        //            rxEventBus.send(events);
        //        });
        StickyGridLayoutManager stickyLayoutManager = new StickyGridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false, usersAdapter);
        //        StickyLayoutManager stickyLayoutManager = new TopSnappedStickyLayoutManager(this, usersAdapter);
        userRecycler.setLayoutManager(stickyLayoutManager);
        //        userRecycler.setLayoutManager(layoutManager);
        userRecycler.setAdapter(usersAdapter);
        usersAdapter.setAllowSelection(true);
        fastScroller.setRecyclerView(userRecycler);
        //        fastScroller.setViewProvider(new DefaultScrollerViewProvider());
        //        fastScroller.setBubbleColor(0xffff0000);
        //        fastScroller.setHandleColor(0xffff0000);
        //        fastScroller.setBubbleTextAppearance(R.style.StyledScrollerTextAppearance);
        //        events = events.mergeWith(Observable.defer(() -> RxRecyclerView.scrollStateChanges(userRecycler)
        //                                                                       .map(integer -> {
        //                                                                           if (integer == SCROLL_STATE_SETTLING) {
        //                                                                               int totalItemCount = layoutManager.getItemCount();
        //                                                                               int firstVisibleItemPosition = layoutManager
        //                                                                                       .findFirstVisibleItemPosition();
        //                                                                               return (layoutManager.getChildCount() +
        // firstVisibleItemPosition) >=
        //                                                                                              totalItemCount &&
        //                                                                                              firstVisibleItemPosition >= 0 &&
        // totalItemCount >=
        //                                                                                       PAGE_SIZE
        //                                                                                      ?
        //                                                                                      new GetPaginatedUsersEvent(viewState.getLastId()) :
        //                                                                                      new GetPaginatedUsersEvent(-1);
        //                                                                           } else {
        //                                                                               return new GetPaginatedUsersEvent(-1);
        //                                                                           }
        //                                                                       })
        //                                                                       .filter(usersNextPageEvent -> usersNextPageEvent.getLastId() != -1)
        //                                                                       .throttleLast(200, TimeUnit.MILLISECONDS)
        //                                                                       .debounce(300, TimeUnit.MILLISECONDS)
        //                                                                       .doOnNext(searchUsersEvent -> Log.d("NextPageEvent", "fired!"))));
        itemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(usersAdapter));
        itemTouchHelper.attachToRecyclerView(userRecycler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //        searchView.setOnCloseListener(() -> {
        //            events = events.mergeWith(Single.<BaseEvent> just(new GetPaginatedUsersEvent(viewState.getLastId()))
        //                    .doOnSuccess(event -> Log.d("CloseSearchViewEvent", "fired!"))
        //                    .toObservable());
        //            rxEventBus.send(events);
        //            return false;
        //        });
        //        events = events.mergeWith(RxSearchView.queryTextChanges(searchView)
        //                                              .filter(charSequence -> !charSequence.toString().isEmpty())
        //                                              .map(query -> new SearchUsersEvent(query.toString()))
        //                                              .throttleLast(100, TimeUnit.MILLISECONDS)
        //                                              .debounce(200, TimeUnit.MILLISECONDS)
        //                                              .doOnNext(searchUsersEvent -> Log.d("SearchEvent", "eventFired")));
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Toggle the selection viewState of an item.
     * <p>
     * <p>If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection viewState
     */
    private void toggleSelection(int position) {
        usersAdapter.toggleSelection(position);
        int count = usersAdapter.getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.selected_list_menu, menu);
        //        events = events.mergeWith(Observable.defer(() -> RxMenuItem.clicks(menu.findItem(R.id.delete_item))
        //                                                                   .map(click -> new DeleteUsersEvent(Observable.fromIterable(usersAdapter
        //                                                                           .getSelectedItems())
        //                                                                                                                .map(itemInfo -> (
        //                                                                                                                        (User) itemInfo
        //                                                                                                                                .getData())
        //                                                                                                                        .getLogin())
        //
        //                                                                                                                .toList().blockingGet()))
        //                                                                   .doOnEach(notification -> {
        //                                                                       actionMode.finish();
        //                                                                       Log.d("DeleteEvent", "fired!");
        //                                                                   })));
        rxEventBus.send(events);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.delete_item).setVisible(true).setEnabled(true);
        toolbar.setVisibility(View.GONE);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return item.getItemId() == R.id.delete_item;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        try {
            usersAdapter.clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        actionMode = null;
        toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }
}
