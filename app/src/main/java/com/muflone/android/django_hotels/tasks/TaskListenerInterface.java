package com.muflone.android.django_hotels.tasks;

/*
 * Listener for AsyncTaskRunner
 */
public interface TaskListenerInterface {
    void onSuccess(AsyncTaskResult result);
    void onFailure(Exception exception);
    void onProgress(int step, int total);
}
