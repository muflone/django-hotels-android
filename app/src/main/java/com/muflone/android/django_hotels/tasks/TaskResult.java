package com.muflone.android.django_hotels.tasks;

import com.muflone.android.django_hotels.api.ApiData;

public class TaskResult {
    public final ApiData data;
    public final Exception exception;

    public TaskResult(ApiData data, Exception exception) {
        this.data = data;
        this.exception = exception;
    }
}
