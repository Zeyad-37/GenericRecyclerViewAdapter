package com.zeyad.gadapter.observables

import com.zeyad.gadapter.GenericAdapter
import com.zeyad.gadapter.GenericViewHolder
import com.zeyad.gadapter.ItemInfo
import com.zeyad.gadapter.OnItemLongClickListener
import com.zeyad.gadapter.checkMainThread
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

class ItemLongClickObservable(private val genericRecyclerViewAdapter: GenericAdapter) : Observable<ClickEvent>() {

    override fun subscribeActual(observer: Observer<in ClickEvent>) {
        if (!observer.checkMainThread()) {
            return
        }
        val listener = Listener(observer)
        observer.onSubscribe(listener)
        genericRecyclerViewAdapter.onItemLongClickListener = listener.onItemLongClickListener
    }

    internal inner class Listener(observer: Observer<in ClickEvent>) : MainThreadDisposable() {
        internal val onItemLongClickListener: OnItemLongClickListener

        init {
            this.onItemLongClickListener = object : OnItemLongClickListener {
                override fun onItemLongClicked(position: Int, itemInfo: ItemInfo<*>, holder: GenericViewHolder<*>): Boolean {
                    if (!isDisposed) {
                        observer.onNext(ClickEvent(position, itemInfo, holder))
                    }
                    return false
                }
            }
        }

        override fun onDispose() {}
    }
}
