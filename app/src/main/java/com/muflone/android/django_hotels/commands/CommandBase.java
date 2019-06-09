package com.muflone.android.django_hotels.commands;

import android.util.Log;

import com.muflone.android.django_hotels.database.models.Command;

public class CommandBase {
    // Common interface for all the Commands to execute from CommandFactory
    public void before() {
        // Log before the execution
        Log.d(this.getClass().getSimpleName(), "Starting execution");
    }

    public void execute(Command command) {
        // Log the execution
        Log.d(this.getClass().getSimpleName(), String.format("Executing command \"%s\"", command.type.id));
    }

    public void after() {
        // Log after the execution
        Log.d(this.getClass().getSimpleName(), "Execution completed");
    }
}
