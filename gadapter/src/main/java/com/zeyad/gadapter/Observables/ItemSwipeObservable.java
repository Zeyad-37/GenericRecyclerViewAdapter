package com.zeyad.gadapter.Observables;

import com.zeyad.gadapter.GenericRecyclerViewAdapter;
import com.zeyad.gadapter.ItemInfo;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.zeyad.gadapter.Utils.checkMainThread;

public final class ItemSwipeObservable extends Observable<ItemInfo> {
    private final GenericRecyclerViewAdapter genericRecyclerViewAdapter;

    public ItemSwipeObservable(GenericRecyclerViewAdapter genericRecyclerViewAdapter) {
        this.genericRecyclerViewAdapter = genericRecyclerViewAdapter;
    }

    @Override
    protected void subscribeActual(Observer<? super ItemInfo> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(observer);
        observer.onSubscribe(listener);
        genericRecyclerViewAdapter.setOnItemSwipeListener(listener.onSwipeListener);
    }

    final class Listener extends MainThreadDisposable {
        private final GenericRecyclerViewAdapter.OnSwipeListener onSwipeListener;

        Listener(final Observer<? super ItemInfo> observer) {
            this.onSwipeListener = new GenericRecyclerViewAdapter.OnSwipeListener() {
                @Override
                public void onItemSwipe(ItemInfo itemInfo) {
                    if (!isDisposed()) {
                        observer.onNext(itemInfo);
                    }
                }
            };
        }

        @Override
        protected void onDispose() {
        }
    }
}
