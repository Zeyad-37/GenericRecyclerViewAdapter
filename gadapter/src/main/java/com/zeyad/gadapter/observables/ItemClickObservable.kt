package com.zeyad.gadapter.observables

import com.zeyad.gadapter.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

class ItemClickObservable(private val genericRecyclerViewAdapter: GenericAdapter) : Observable<ClickEvent>() {

    override fun subscribeActual(observer: Observer<in ClickEvent>) {
        if (!observer.checkMainThread()) {
            return
        }
        val listener = Listener(observer)
        observer.onSubscribe(listener)
        genericRecyclerViewAdapter.onItemClickListener = listener.onItemClickListener
    }

    internal inner class Listener(observer: Observer<in ClickEvent>) : MainThreadDisposable() {
        internal val onItemClickListener: OnItemClickListener

        init {
            this.onItemClickListener = object : OnItemClickListener {
                override fun onItemClicked(position: Int, itemInfo: ItemInfo, holder: GenericViewHolder<*>) {
                    if (!isDisposed) {
                        observer.onNext(ClickEvent(position, itemInfo, holder))
                    }
                }
            }
        }

        override fun onDispose() {}
    }
}
