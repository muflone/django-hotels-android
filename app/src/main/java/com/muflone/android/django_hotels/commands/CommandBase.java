package com.muflone.android.django_hotels.commands;

import android.content.Context;
import android.util.Log;

import com.muflone.android.django_hotels.database.models.Command;

public class CommandBase {
    // Common interface for all the Commands to execute from CommandFactory
    protected final Context context;
    protected final Command command;

    public CommandBase(Context context, Command command) {
        this.context = context;
        this.command = command;
        Log.d(this.getClass().getSimpleName(), String.format("Initializing command \"%s\"", command.type.id));
    }

    public void before() {
        // Log before the execution
        Log.d(this.getClass().getSimpleName(), String.format("Starting execution for \"%s\"", command.type.id));
    }

    public void execute() {
        // Log the execution
        Log.d(this.getClass().getSimpleName(), String.format("Executing command \"%s\"", command.type.id));
    }

    public void after() {
        // Log after the execution
        Log.d(this.getClass().getSimpleName(), String.format("Execution completed for \"%s\"", command.type.id));
    }
}
