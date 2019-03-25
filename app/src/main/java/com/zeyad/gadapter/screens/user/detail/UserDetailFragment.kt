package com.zeyad.gadapter.screens.user.detail

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zeyad.gadapter.GenericRecyclerViewAdapter
import com.zeyad.gadapter.GenericViewHolder
import com.zeyad.gadapter.R
import com.zeyad.gadapter.screens.BaseFragment
import com.zeyad.gadapter.screens.user.list.UserListActivity
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.view.ErrorMessageFactory
import com.zeyad.rxredux.core.view.UI_MODEL
import io.reactivex.Observable
import kotlinx.android.synthetic.main.user_detail.*
import kotlinx.android.synthetic.main.view_progress.*
import org.koin.android.viewmodel.ext.android.getViewModel
import java.util.*

/**
 * A fragment representing a single Repository detail screen. This fragment is either contained in a
 * [UserListActivity] in two-pane mode (on tablets) or a [UserDetailActivity] on
 * handsets.
 */
class UserDetailFragment : BaseFragment<UserDetailState, UserDetailVM>() {

    private lateinit var repositoriesAdapter: GenericRecyclerViewAdapter

    private val requestListener = object : RequestListener<Drawable> {
        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
                                     dataSource: DataSource?, isFirstResource: Boolean): Boolean =
                glideRequestListenerCore()

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
                                  isFirstResource: Boolean): Boolean =
                glideRequestListenerCore()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
        //        setSharedElementReturnTransition(null); // supply the correct element for return transition
    }

    override fun errorMessageFactory(): ErrorMessageFactory = { throwable, _ -> throwable.localizedMessage }

    override fun initialize() {
        viewModel = getViewModel()
    }

    override fun initialState(): UserDetailState = arguments?.getParcelable(UI_MODEL)!!

    override fun events(): Observable<BaseEvent<*>> = Observable.just(viewState?.user?.login?.let { GetReposEvent(it) })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.user_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView_repositories.layoutManager = LinearLayoutManager(context)
        repositoriesAdapter = object : GenericRecyclerViewAdapter(
                requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, ArrayList()) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
                return RepositoryViewHolder(layoutInflater.inflate(viewType, parent, false))
            }
        }
        recyclerView_repositories.adapter = repositoriesAdapter
    }

    override fun renderSuccessState(successState: UserDetailState) {
        repositoriesAdapter.setDataList(successState.repos, null)
        val user = successState.user
        if (successState.isTwoPane) {
            (activity as UserListActivity).let { activity ->
                val appBarLayout = activity.findViewById<Toolbar>(R.id.toolbar)
                if (appBarLayout != null) {
                    appBarLayout.title = user.login
                }
                if (user.avatarUrl.isNotBlank()) {
                    Glide.with(activity).load(user.avatarUrl).listener(requestListener)
                            .into(activity.getImageViewAvatar())
                }
            }
        } else {
            (activity as UserDetailActivity).let { activity ->
                val appBarLayout = activity.getCollapsingToolbarLayout()
                appBarLayout.title = user.login
                if (user.avatarUrl.isNotBlank()) {
                    Glide.with(activity).load(user.avatarUrl).listener(requestListener)
                            .into(activity.getImageViewAvatar())
                }
            }
        }
    }

    internal fun glideRequestListenerCore(): Boolean {
        requireActivity().supportStartPostponedEnterTransition()
        return false
    }

    override fun toggleViews(isLoading: Boolean, event: BaseEvent<*>) {
        linear_layout_loader.bringToFront()
        linear_layout_loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun showError(errorMessage: String, event: BaseEvent<*>) {
        showErrorSnackBar(errorMessage, linear_layout_loader, Snackbar.LENGTH_LONG)
    }

    companion object {

        fun newInstance(userDetailState: UserDetailState): UserDetailFragment =
                UserDetailFragment().apply { arguments = Bundle().apply { putParcelable(UI_MODEL, userDetailState) } }
    }
}
