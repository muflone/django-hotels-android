package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;

import com.muflone.android.django_hotels.database.models.Command;

public class CommandDummy extends CommandBase {
    // This is a dummy command that does nothing
    public CommandDummy(Activity activity, Context context, Context applicationContext, Command command) {
        super(activity, context, applicationContext, command);
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
