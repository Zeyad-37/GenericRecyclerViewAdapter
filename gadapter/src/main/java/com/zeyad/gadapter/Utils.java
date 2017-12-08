package com.zeyad.gadapter;

import android.os.Looper;

import io.reactivex.Observer;

/**
 * @author ZIaDo on 12/8/17.
 */
public class Utils {

    public static boolean checkMainThread(Observer<?> observer) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            observer.onError(new IllegalStateException(
                    "Expected to be called on the main thread but was " + Thread.currentThread().getName()));
            return false;
        }
        return true;
    }
}
