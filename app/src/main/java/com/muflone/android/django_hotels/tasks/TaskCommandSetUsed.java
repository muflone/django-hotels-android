package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.CommandUsage;

public class TaskCommandSetUsed extends AsyncTask<CommandUsage, Void, Void> {
    private final Singleton singleton = Singleton.getInstance();

    @Override
    protected Void doInBackground(CommandUsage... params) {
        CommandUsage commandUsage = params[0];
        this.singleton.database.commandUsageDao().update(commandUsage);
        return null;
    }
}
