package com.muflone.android.django_hotels.commands;

import com.muflone.android.django_hotels.database.models.Command;

public interface CommandInterface {
    // Common interface for all the Commands to execute from CommandFactory
    void execute(Command command);
}
