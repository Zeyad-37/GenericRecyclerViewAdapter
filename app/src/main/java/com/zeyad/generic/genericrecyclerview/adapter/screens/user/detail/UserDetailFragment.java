package com.zeyad.generic.genericrecyclerview.adapter.screens.user.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.zeyad.gadapter.GenericRecyclerViewAdapter;
import com.zeyad.gadapter.ItemInfo;
import com.zeyad.generic.genericrecyclerview.R;
import com.zeyad.generic.genericrecyclerview.adapter.screens.BaseFragment;
import com.zeyad.generic.genericrecyclerview.adapter.screens.user.list.User;
import com.zeyad.generic.genericrecyclerview.adapter.screens.user.list.UserListActivity;
import com.zeyad.generic.genericrecyclerview.adapter.screens.utils.Utils;
import com.zeyad.rxredux.core.BaseEvent;
import com.zeyad.rxredux.core.view.ErrorMessageFactory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

import static com.zeyad.rxredux.core.view.BaseViewKt.UI_MODEL;

/**
 * A fragment representing a single Repository detail screen. This fragment is either contained in a
 * {@link UserListActivity} in two-pane mode (on tablets) or a {@link UserDetailActivity} on
 * handsets.
 */
public class UserDetailFragment extends BaseFragment<UserDetailState, UserDetailVM> {
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;

    @BindView(R.id.recyclerView_repositories)
    RecyclerView recyclerViewRepositories;

    private GenericRecyclerViewAdapter repositoriesAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public UserDetailFragment() {
    }

    public static UserDetailFragment newInstance(UserDetailState userDetailState) {
        UserDetailFragment userDetailFragment = new UserDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(UI_MODEL, Parcels.wrap(userDetailState));
        userDetailFragment.setArguments(bundle);
        return userDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(
                    TransitionInflater.from(getContext())
                            .inflateTransition(android.R.transition.move));
        }
        //        setSharedElementReturnTransition(null); // supply the correct element for return transition
    }

    @Override
    public ErrorMessageFactory errorMessageFactory() {
        return (throwable, baseEvent) -> throwable.getLocalizedMessage();
    }

    @Override
    public void initialize() {
        viewModel = ViewModelProviders.of(this).get(UserDetailVM.class);
        //        events = Observable.just(new GetReposEvent(viewState.getUser().getLogin()));
    }

    @NotNull
    @Override
    public UserDetailState initialState() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return Parcels.unwrap(arguments.getParcelable(UI_MODEL));
        } else null;
    }

    @Override
    public Observable<BaseEvent> events() {
        return null;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);
        ButterKnife.bind(this, rootView);
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        recyclerViewRepositories.setLayoutManager(new LinearLayoutManager(getContext()));
        repositoriesAdapter = new GenericRecyclerViewAdapter((LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)) {
            @Override
            public GenericViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RepositoryGenericViewHolder(getLayoutInflater().inflate(viewType, parent, false));
            }
        };
        recyclerViewRepositories.setAdapter(repositoriesAdapter);
    }

    @Override
    public void renderSuccessState(UserDetailState userDetailState) {
        User user = userDetailState.getUser();
        List<Repository> repoModels = userDetailState.getRepos();
        if (Utils.isNotEmpty(repoModels)) {
            repositoriesAdapter.animateTo(Observable.fromIterable(repoModels)
                    .map(repository -> new ItemInfo(repository, R.layout.repo_item_layout))
                    .toList(repoModels.size()).blockingGet());
        }
        if (user != null) {
            if (userDetailState.isTwoPane()) {
                UserListActivity activity = (UserListActivity) getActivity();
                if (activity != null) {
                    Toolbar appBarLayout = activity.findViewById(R.id.toolbar);
                    if (appBarLayout != null) {
                        appBarLayout.setTitle(user.getLogin());
                    }
                    if (Utils.isNotEmpty(user.getAvatarUrl())) {
                        Glide.with(requireContext())
                                .load(user.getAvatarUrl())
                                .into(activity.imageViewAvatar);
                    }
                }
            } else {
                UserDetailActivity activity = (UserDetailActivity) getActivity();
                if (activity != null) {
                    CollapsingToolbarLayout appBarLayout = activity.collapsingToolbarLayout;
                    if (appBarLayout != null) {
                        appBarLayout.setTitle(user.getLogin());
                    }
                    if (Utils.isNotEmpty(user.getAvatarUrl())) {
                        Glide.with(requireContext())
                                .load(user.getAvatarUrl())
                                .into(activity.imageViewAvatar);
                    }
                }
            }
        }
        //        applyPalette();
    }

    @Override
    public void showError(@NotNull String message, @NotNull BaseEvent<?> baseEvent) {
        showErrorSnackBar(message, loaderLayout, Snackbar.LENGTH_LONG);
    }

    @Override
    public void toggleViews(boolean toggle, @NotNull BaseEvent<?> baseEvent) {
        loaderLayout.bringToFront();
        loaderLayout.setVisibility(toggle ? View.VISIBLE : View.GONE);
    }

    private void applyPalette() {
        if (Utils.hasM()) {
            UserDetailActivity activity = (UserDetailActivity) getActivity();
            BitmapDrawable drawable = (BitmapDrawable) activity.imageViewAvatar.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            //            Palette.from(bitmap).generate(palette -> activity.findViewById(R.id.coordinator_detail)
            //                                                             .setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY)
            // -> {
            //                        if (v.getHeight() == scrollX) {
            //                            activity.toolbar.setTitleTextColor(palette.getLightVibrantColor(Color.TRANSPARENT));
            //                            activity.toolbar
            //                                    .setBackground(new ColorDrawable(palette.getLightVibrantColor(Color.TRANSPARENT)));
            //                        } else if (scrollY == 0) {
            //                            activity.toolbar.setTitleTextColor(0);
            //                            activity.toolbar.setBackground(null);
            //                        }
            //                    }));
        }
    }
}
