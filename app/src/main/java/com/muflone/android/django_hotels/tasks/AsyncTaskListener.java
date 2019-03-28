package com.muflone.android.django_hotels.tasks;

/*
 * Listener for AsyncTaskRunner
 */
public interface AsyncTaskListener<T> {
    void onSuccess(T instance);
    void onFailure(Exception e);
}
