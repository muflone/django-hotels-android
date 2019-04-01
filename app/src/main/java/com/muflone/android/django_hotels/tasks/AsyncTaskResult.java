package com.muflone.android.django_hotels.tasks;

public class AsyncTaskResult<T> {
    public final T data;
    public final Exception exception;

    public AsyncTaskResult(T data, Exception exception) {
        this.data = data;
        this.exception = exception;
    }
}
