package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.Command;
import com.muflone.android.django_hotels.database.models.CommandUsage;

import org.json.JSONException;

public class CommandCommandUsed extends CommandBase {
    /**
     * This Command changes a Command usage count
     *
     * The command must have the following arguments:
     * id: the ID of the command to set
     * used: the number of uses to set
     */
    private final Singleton singleton = Singleton.getInstance();

    public CommandCommandUsed(Activity activity, Context context, Command command) {
        super(activity, context, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            CommandUsage[] commandUsages = this.singleton.apiData.commandsUsageMap.values().toArray(new CommandUsage[0]);
            int commandId = command.command.getInt("id");
            int commandUsed = command.command.getInt("used");
            // Find CommandUsages to update
            if (commandId == -1) {
                // Update every CommandUsages
                new CommandUsedUpdateDatabaseTask(commandUsed).execute(commandUsages);
            } else {
                // Find the CommandUsage to update
                for (CommandUsage commandUsage : commandUsages) {
                    if (commandUsage.id == commandId) {
                        new CommandUsedUpdateDatabaseTask(commandUsed).execute(commandUsage);
                        break;
                    }
                }
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    private static class CommandUsedUpdateDatabaseTask extends AsyncTask<CommandUsage, Void, Void> {
        private final Singleton singleton = Singleton.getInstance();
        private final int used;

        public CommandUsedUpdateDatabaseTask(int used) {
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
}
