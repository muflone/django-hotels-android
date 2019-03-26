package com.muflone.android.django_hotels;

/*
 * Listener for AsyncTaskRunner
 */
public interface AsyncTaskListener<T> {
    void onSuccess(T instance);
    void onFailure(Exception e);
}
