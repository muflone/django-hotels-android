package com.muflone.android.django_hotels.commands;

import android.util.Log;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.Command;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class CommandFactory {
    private final String TAG = getClass().getName();

    public void executeCommands(String context) {
        // Process every command for the current context
        for (Command command : Singleton.getInstance().apiData.commandsMap.values()) {
            // Process only the commands in the current context
            if (command.context.equals(context)) {
                // Skip attempts to execute commands of the factory type
                if (! command.type.type.equals(this.getClass().getSimpleName())) {
                    try {
                        Class<?> commandClass = Class.forName(String.format("%s.%s",
                                Objects.requireNonNull(this.getClass().getPackage()).getName(),
                                command.type.type));
                        Constructor<?> commandConstructor = commandClass.getConstructor();
                        CommandInterface commandInstance = (CommandInterface) commandConstructor.newInstance();
                        commandInstance.execute(command);
                    } catch (ClassNotFoundException e) {
                        Log.w(TAG, String.format("Command class %s not found", command.type.type));
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        Log.w(TAG, String.format("Missing constructor for class %s", command.type.type));
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.w(TAG, "Attempting to execute a factory command, skipped");
                }
            }
        }
    }
}
