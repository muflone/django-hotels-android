package com.muflone.android.django_hotels;

/*
 * Listener for AsyncTaskRunner
 */
public interface AsyncTaskRunnerListener<T> {
    public void onSuccess(T instance);
    public void onFailure(Exception e);
}
