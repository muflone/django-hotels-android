/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2019 Fabio Castelli
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
import android.util.Log;

import com.muflone.android.django_hotels.database.models.Command;

@SuppressWarnings("WeakerAccess")
public class CommandBase {
    // Common interface for all the Commands to execute from CommandFactory
    protected final Activity activity;
    protected final Context context;
    protected final Command command;

    public CommandBase(Activity activity, Context context, Command command) {
        this.activity = activity;
        this.context = context;
        this.command = command;
        Log.d(this.getClass().getSimpleName(), String.format("Initializing command \"%s\"", command.name));
    }

    public void before() {
        // Log before the execution
        Log.d(this.getClass().getSimpleName(), String.format("Before execution for \"%s\"", command.name));
    }

    public void execute() {
        // Log the execution
        Log.d(this.getClass().getSimpleName(), String.format("Executing command \"%s\"", command.name));
    }

    public void after() {
        // Log after the execution
        Log.d(this.getClass().getSimpleName(), String.format("Execution completed for \"%s\"", command.name));
    }
}
