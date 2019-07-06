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
