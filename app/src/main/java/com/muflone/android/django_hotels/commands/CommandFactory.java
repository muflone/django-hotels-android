package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.database.models.Command;
import com.muflone.android.django_hotels.database.models.CommandUsage;
import com.muflone.android.django_hotels.tasks.TaskCommandSetUsed;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;

public class CommandFactory {
    private final String TAG = getClass().getSimpleName();
    private final Singleton singleton = Singleton.getInstance();

    public void executeCommands(Activity activity, Context context, String contextType) {
        Log.d(this.TAG, String.format("Processing commands for context %s", contextType));
        // Process every command for the current context
        ArrayList<Command> commandsList =  this.singleton.apiData.getCommandsByContext(contextType);
        for (Command command : commandsList) {
            // Skip attempts to execute commands of the factory type
            if (! command.type.equals(this.getClass().getSimpleName())) {
                try {
                    Class<?> commandClass = Class.forName(String.format("%s.Command%s",
                            Objects.requireNonNull(this.getClass().getPackage()).getName(),
                            command.type));
                    Constructor<?> commandConstructor = commandClass.getConstructor(
                            Activity.class, Context.class, Command.class);
                    CommandBase commandInstance = (CommandBase) commandConstructor.newInstance(
                            activity, context, command);
                    CommandUsage commandUsage = Objects.requireNonNull(this.singleton.apiData.commandsUsageMap.get(command.id));
                    if (command.uses == 0 | commandUsage.used < command.uses) {
                        commandInstance.before();
                        commandInstance.execute();
                        commandInstance.after();
                        // Update CommandUsage count
                        commandUsage.used = command.uses == 0 ? 0 : commandUsage.used + 1;
                        new TaskCommandSetUsed().execute(commandUsage);
                    }
                } catch (ClassNotFoundException exception) {
                    Log.w(this.TAG, String.format("Command class %s not found", command.type));
                    exception.printStackTrace();
                } catch (NoSuchMethodException exception) {
                    Log.w(this.TAG, String.format("Missing constructor for class %s", command.type));
                    exception.printStackTrace();
                } catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                } catch (InstantiationException exception) {
                    exception.printStackTrace();
                } catch (InvocationTargetException exception) {
                    exception.printStackTrace();
                }
            } else {
                Log.w(this.TAG, "Attempting to execute a factory command, skipped");
            }
        }
        Log.d(this.TAG, String.format("Completed commands for context %s", contextType));
        // Apply a delay for APP END context
        if (contextType.equals(CommandConstants.CONTEXT_APP_END) && commandsList.size() > 0) {
            long delay = this.singleton.settings.getLong(CommandConstants.SETTING_APP_EXIT_COMMANDS_DELAY, 0);
            Log.d(this.TAG, String.format("Applying %s of %d milliseconds",
                    CommandConstants.SETTING_APP_EXIT_COMMANDS_DELAY,
                    delay));
            Utility.sleep(delay);
        }
    }
}
