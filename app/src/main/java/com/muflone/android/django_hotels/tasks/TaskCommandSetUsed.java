package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.CommandUsage;

public class TaskCommandSetUsed extends AsyncTask<CommandUsage, Void, Void> {
    private final Singleton singleton = Singleton.getInstance();
    private final int used;

    public TaskCommandSetUsed(int used) {
        this.used = used;
    }

    @Override
    protected Void doInBackground(CommandUsage... params) {
        for (CommandUsage commandUsage : params) {
            commandUsage.used = this.used;
            this.singleton.database.commandUsageDao().update(commandUsage);
        }
        return null;
    }
}
