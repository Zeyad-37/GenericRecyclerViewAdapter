package com.zeyad.gadapter.fastscroll.viewprovider;

/**
 * Created by Michal on 11/08/16.
 * Extending classes should use this interface to get notified about events that occur to the
 * fastScroller elements (handle and bubble) and react accordingly. See {@link DefaultBubbleBehavior}
 * for an example.
 */
interface ViewBehavior {
    void onHandleGrabbed();

    void onHandleReleased();

    void onScrollStarted();

    void onScrollFinished();
}
