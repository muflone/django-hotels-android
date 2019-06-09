package com.muflone.android.django_hotels.commands;

import com.muflone.android.django_hotels.database.models.Command;

public class CommandDummy extends CommandBase {
    // This is a dummy command that does nothing

    @Override
    public void before() {
        super.before();
    }

    @Override
    public void execute(Command command) {
        super.execute(command);
    }

    @Override
    public void after() {
        super.after();
    }
}
