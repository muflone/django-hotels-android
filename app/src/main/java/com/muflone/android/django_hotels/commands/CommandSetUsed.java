/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2020 Fabio Castelli
 *     License: GPL-3+
 * Source code: https://github.com/muflone/django-hotels-android
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.Command;
import com.muflone.android.django_hotels.database.models.CommandUsage;
import com.muflone.android.django_hotels.tasks.TaskCommandSetUsed;

import org.json.JSONException;

public class CommandSetUsed extends CommandBase {
    /**
     * This Command changes a Command usage count
     *
     * The command must have the following arguments:
     * id: the ID of the command to set
     * used: the number of uses to set
     */
    private final Singleton singleton = Singleton.getInstance();

    public CommandSetUsed(Activity activity, Context context, Command command) {
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
            if (commandId == CommandConstants.SET_USED_SET_ALL_COMMAND_USAGES) {
                // Update every CommandUsages
                new TaskCommandSetUsed(commandUsed).execute(commandUsages);
            } else {
                // Find the CommandUsage to update
                for (CommandUsage commandUsage : commandUsages) {
                    if (commandUsage.id == commandId) {
                        new TaskCommandSetUsed(commandUsed).execute(commandUsage);
                        break;
                    }
                }
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
