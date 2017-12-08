package com.zeyad.gadapter.Observables;

import com.zeyad.gadapter.GenericRecyclerViewAdapter;
import com.zeyad.gadapter.ItemInfo;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.zeyad.gadapter.Utils.checkMainThread;

public final class ItemLongClickObservable extends Observable<ClickEvent> {
    private final GenericRecyclerViewAdapter genericRecyclerViewAdapter;

    public ItemLongClickObservable(GenericRecyclerViewAdapter genericRecyclerViewAdapter) {
        this.genericRecyclerViewAdapter = genericRecyclerViewAdapter;
    }

    @Override
    protected void subscribeActual(Observer<? super ClickEvent> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(observer);
        observer.onSubscribe(listener);
        genericRecyclerViewAdapter.setOnItemLongClickListener(listener.onItemLongClickListener);
    }

    final class Listener extends MainThreadDisposable {
        private final GenericRecyclerViewAdapter.OnItemLongClickListener onItemLongClickListener;

        Listener(final Observer<? super ClickEvent> observer) {
            this.onItemLongClickListener = new GenericRecyclerViewAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClicked(int position, ItemInfo itemInfo, GenericRecyclerViewAdapter.ViewHolder holder) {
                    if (!isDisposed()) {
                        observer.onNext(new ClickEvent(position, itemInfo, holder));
                    }
                    return false;
                }
            };
        }

        @Override
        protected void onDispose() {
        }
    }
}
