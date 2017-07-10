package com.amagh.bakemate.utils.idling;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hnoct on 7/9/2017.
 */

public class SimpleIdlingResource implements IdlingResource {
    @Nullable
    private volatile ResourceCallback mCallback;
    private AtomicBoolean isIdleNow = new AtomicBoolean(true);

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return this.isIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }

    /**
     * Sets the idle state of the IdlingResource and triggers the ResourceCallback if isIdleNow is
     * true.
     *
     * @param idleState    False if there are pending operations, true if idle
     */
    public void setIdleState(boolean idleState) {
        this.isIdleNow.set(idleState);

        if (this.isIdleNow.get() && mCallback != null) {
            mCallback.onTransitionToIdle();
        }
    }
}
