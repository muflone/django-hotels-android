package com.muflone.android.django_hotels.tasks;

import com.muflone.android.django_hotels.api.ApiData;

public class AsyncTaskResult {
    public final ApiData data;
    public final Exception exception;

    public AsyncTaskResult(ApiData data, Exception exception) {
        this.data = data;
        this.exception = exception;
    }
}
