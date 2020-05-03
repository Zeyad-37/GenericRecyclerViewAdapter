package com.zeyad.app.screens.detail

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zeyad.app.R
import com.zeyad.app.screens.list.UserListActivity
import com.zeyad.app.screens.list.UserListActivity2
import com.zeyad.gadapter.GenericRecyclerViewAdapter
import com.zeyad.gadapter.GenericViewHolder
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.Message
import com.zeyad.rxredux.core.view.IBaseFragment
import com.zeyad.rxredux.core.view.P_MODEL
import io.reactivex.Observable
import kotlinx.android.synthetic.main.user_detail.*
import kotlinx.android.synthetic.main.view_progress.*
import org.koin.android.viewmodel.ext.android.getViewModel

/**
 * A fragment representing a single Repository detail screen. This fragment is either contained in a
 * [UserListActivity] in two-pane mode (on tablets) or a [UserDetailActivity2] on
 * handsets.
 */
@SuppressLint("ValidFragment")
class UserDetailFragment2(override var viewModel: UserDetailVM?,
                          override var viewState: UserDetailState?) : androidx.fragment.app.Fragment(), IBaseFragment<UserDetailState, Unit, UserDetailVM> {
    override fun applyEffect(effectBundle: Unit) {
    }

    constructor() : this(null, null)

    private lateinit var repositoriesAdapter: GenericRecyclerViewAdapter

    private val requestListener = object : RequestListener<String, GlideDrawable> {
        override fun onException(e: Exception, model: String, target: Target<GlideDrawable>,
                                 isFirstResource: Boolean): Boolean {
            return glideRequestListenerCore()
        }

        override fun onResourceReady(resource: GlideDrawable, model: String, target: Target<GlideDrawable>,
                                     isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
            return glideRequestListenerCore()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateImpl(savedInstanceState)
        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
        //        setSharedElementReturnTransition(null); // supply the correct element for return transition
    }

    override fun onStart() {
        super.onStart()
        onStartImpl()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        onSaveInstanceStateImpl(outState)
        super.onSaveInstanceState(outState)
    }

    override fun initialize() {
        viewModel = getViewModel()
        viewState = arguments?.getParcelable(P_MODEL)!!
    }

    override fun events(): Observable<BaseEvent<*>> = Observable.just(GetReposEvent((viewState as IntentBundleState).user.login))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.user_detail, container, false)
    }

    override fun setupUI(isNew: Boolean) {
        recyclerView_repositories.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        repositoriesAdapter = object : GenericRecyclerViewAdapter() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<*> {
                return RepositoryViewHolder(layoutInflater.inflate(viewType, parent, false))
            }
        }
        recyclerView_repositories.adapter = repositoriesAdapter
    }

    override fun renderSuccessState(successState: UserDetailState) {
        when (successState) {
            is FullDetailState -> {
                repositoriesAdapter.setDataList(successState.repos, null)
                val user = successState.user
                if (successState.isTwoPane) {
                    (activity as UserListActivity2).let { activity ->
                        val appBarLayout = activity.findViewById<Toolbar>(R.id.toolbar)
                        if (appBarLayout != null) {
                            appBarLayout.title = user.login
                        }
                        if (user.avatarUrl.isNotBlank()) {
                            Glide.with(context).load(user.avatarUrl).dontAnimate().listener(requestListener)
                                    .into(activity.getImageViewAvatar())
                        }
                    }
                } else {
                    (activity as UserDetailActivity).let { activity ->
                        val appBarLayout = activity.getCollapsingToolbarLayout()
                        appBarLayout.title = user.login
                        if (user.avatarUrl.isNotBlank()) {
                            Glide.with(context).load(user.avatarUrl).dontAnimate().listener(requestListener)
                                    .into(activity.getImageViewAvatar())
                        }
                    }
                }
            }
            is NavigateFromDetail -> startActivity(successState.intent)
        }
    }

    internal fun glideRequestListenerCore(): Boolean {
        activity?.supportStartPostponedEnterTransition()
        return false
    }

    override fun toggleViews(isLoading: Boolean, event: BaseEvent<*>) {
        linear_layout_loader.bringToFront()
        linear_layout_loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun showError(errorMessage: Message, event: BaseEvent<*>) {
//        showErrorSnackBar(errorMessage, linear_layout_loader, Snackbar.LENGTH_LONG)
    }

    companion object {

        fun newInstance(userDetailState: UserDetailState): UserDetailFragment2 =
                UserDetailFragment2().apply { arguments = Bundle().apply { putParcelable(P_MODEL, userDetailState) } }
    }
}
