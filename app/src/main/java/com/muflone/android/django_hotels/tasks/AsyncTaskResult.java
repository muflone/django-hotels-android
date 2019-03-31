package com.muflone.android.django_hotels.tasks;

public class AsyncTaskResult<T> {
    public T data;
    public AsyncTaskListener callback;
    public Exception exception;

    public AsyncTaskResult(T data, AsyncTaskListener callback, Exception exception) {
        this.data = data;
        this.callback = callback;
        this.exception = exception;
    }
}
