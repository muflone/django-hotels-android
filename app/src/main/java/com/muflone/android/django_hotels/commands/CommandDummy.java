package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;

import com.muflone.android.django_hotels.database.models.Command;

@SuppressWarnings("EmptyMethod")
public class CommandDummy extends CommandBase {
    /**
     * This is a dummy command that does nothing
     */
    public CommandDummy(Activity activity, Context context, Command command) {
        super(activity, context, command);
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
