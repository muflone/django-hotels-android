package com.muflone.android.django_hotels.commands;

import android.content.Context;

import com.muflone.android.django_hotels.database.models.Command;

public class CommandDummy extends CommandBase {
    // This is a dummy command that does nothing
    public CommandDummy(Context context, Command command) {
        super(context, command);
    }

    @Override
    public void before() {
        super.before();
    }

    @Override
    public void execute() {
        super.execute();
    }

    @Override
    public void after() {
        super.after();
    }
}
