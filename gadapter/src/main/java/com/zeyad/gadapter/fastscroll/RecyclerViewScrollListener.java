package com.zeyad.gadapter.fastscroll;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for updating the handle / bubble position when user scrolls the {@link android.support.v7.widget.RecyclerView}.
 */
class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private final FastScroller scroller;
    private List<ScrollerListener> listeners = new ArrayList<>();
    private int oldScrollState = RecyclerView.SCROLL_STATE_IDLE;

    RecyclerViewScrollListener(FastScroller scroller) {
        this.scroller = scroller;
    }

    void addScrollerListener(ScrollerListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newScrollState) {
        super.onScrollStateChanged(recyclerView, newScrollState);
        if (newScrollState == RecyclerView.SCROLL_STATE_IDLE && oldScrollState != RecyclerView.SCROLL_STATE_IDLE) {
            scroller.getViewProvider().onScrollFinished();
        } else if (newScrollState != RecyclerView.SCROLL_STATE_IDLE && oldScrollState == RecyclerView.SCROLL_STATE_IDLE) {
            scroller.getViewProvider().onScrollStarted();
        }
        oldScrollState = newScrollState;
    }

    @Override
    public void onScrolled(RecyclerView rv, int dx, int dy) {
        if (scroller.shouldUpdateHandlePosition()) {
            updateHandlePosition(rv);
        }
    }

    void updateHandlePosition(RecyclerView rv) {
        float relativePos;
        if (scroller.isVertical()) {
            int offset = rv.computeVerticalScrollOffset();
            int extent = rv.computeVerticalScrollExtent();
            int range = rv.computeVerticalScrollRange();
            relativePos = offset / (float) (range - extent);
        } else {
            int offset = rv.computeHorizontalScrollOffset();
            int extent = rv.computeHorizontalScrollExtent();
            int range = rv.computeHorizontalScrollRange();
            relativePos = offset / (float) (range - extent);
        }
        scroller.setScrollerPosition(relativePos);
        notifyListeners(relativePos);
    }

    private void notifyListeners(float relativePos) {
        for (ScrollerListener listener : listeners) { listener.onScroll(relativePos); }
    }

    interface ScrollerListener {
        void onScroll(float relativePos);
    }
}
