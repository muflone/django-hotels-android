package com.muflone.android.django_hotels.tasks;

/*
 * Listener for AsyncTaskRunner
 */
public interface AsyncTaskListener {
    void onSuccess(AsyncTaskResult result);
    void onFailure(Exception e);
}
