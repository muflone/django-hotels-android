package com.muflone.android.django_hotels.tasks;

public class AsyncTaskResult<T> {
    public final T data;
    public final AsyncTaskListener callback;
    public final Exception exception;

    public AsyncTaskResult(T data, AsyncTaskListener callback, Exception exception) {
        this.data = data;
        this.callback = callback;
        this.exception = exception;
    }
}
