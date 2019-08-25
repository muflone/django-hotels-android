/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2019 Fabio Castelli
 *     License: GPL-3+
 * Source code: https://github.com/muflone/django-hotels-android
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.Command;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommandDialog extends CommandBase {
    /**
     * This Command show a dialog window
     *
     * The command must have the following arguments:
     * type: must be one of the following:
     *   DIALOG_TYPE_MESSAGE for text message
     *   DIALOG_TYPE_INPUT for text input
     *   DIALOG_TYPE_LIST for single choice
     * cancelable: boolean value to make the dialog cancelable
     * ok_button: title for the positive button
     * ok_variable: variable name to store positive button result
     * ok_result: result for positive button
     * no_button: title for the negative button
     * no_variable: variable name to store negative button result
     * no_result: result for negative button
     * title: the dialog tag
     * content: the content
     */

    private final Singleton singleton = Singleton.getInstance();

    public CommandDialog(Activity activity, Context context, Command command) {
        super(activity, context, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
            final JSONObject jsonObject = this.command.command;
            final EditText inputView = new EditText(this.context);
            final String dialogType = jsonObject.getString("type");
            final String okVariableName = jsonObject.getString("ok_button").length() > 0 ?
                    jsonObject.getString("ok_variable") : null;
            final String noVariableName = jsonObject.getString("no_button").length() > 0 ?
                    jsonObject.getString("no_variable") : null;
            builder.setTitle(jsonObject.getString("title"));
            builder.setCancelable(jsonObject.getBoolean("cancelable"));
            // Prepare positive button
            if (jsonObject.getString("ok_button").length() > 0) {
                System.out.println(this.singleton.settings.getString(okVariableName, ""));
                // Data must not be saved for DIALOG_TYPE_LIST
                // as the data will be saved with a simple click, without any button to press
                if (! dialogType.equals(CommandConstants.DIALOG_TYPE_LIST)) {
                    String result = jsonObject.getString("ok_result");
                    builder.setPositiveButton(jsonObject.getString("ok_button"), (dialog, which) -> {
                        if (okVariableName != null) {
                            // Assign result to variableName
                            if (dialogType.equals(CommandConstants.DIALOG_TYPE_INPUT)) {
                                this.singleton.settings.setValue(okVariableName, inputView.getText().toString());
                            } else {
                                this.singleton.settings.setValue(okVariableName, result);
                            }
                        }
                    });
                }
            }
            // Prepare negative button
            if (jsonObject.getString("no_button").length() > 0) {
                System.out.println(this.singleton.settings.getString(noVariableName, ""));
                String result = jsonObject.getString("no_result");
                builder.setNegativeButton(jsonObject.getString("no_button"), (dialog, which) -> {
                    if (noVariableName != null) {
                        // Assign result to variableName
                        this.singleton.settings.setValue(noVariableName, result);
                    }
                });
            }
            switch (jsonObject.getString("type")) {
                case CommandConstants.DIALOG_TYPE_MESSAGE:
                    builder.setMessage(jsonObject.getString("content"));
                    break;
                case CommandConstants.DIALOG_TYPE_INPUT:
                    inputView.setText(jsonObject.getString("content"));
                    builder.setView(inputView);
                    break;
                case CommandConstants.DIALOG_TYPE_LIST:
                    // Prepares list items
                    ArrayList<String> listItemKeys = new ArrayList<>();
                    ArrayList<String> listItemValue = new ArrayList<>();
                    JSONArray jsonItems = jsonObject.getJSONArray("content");
                    for (int i = 0; i < jsonItems.length(); i++) {
                        JSONObject jsonItem = jsonItems.getJSONObject(i);
                        listItemKeys.add(jsonItem.getString("id"));
                        listItemValue.add(jsonItem.getString("value"));
                    }
                    builder.setItems(listItemValue.toArray(new String[0]), (dialog, position) -> {
                        this.singleton.settings.setValue(okVariableName, listItemKeys.get(position));
                        dialog.dismiss();
                    });
                    break;
                case "d":
                    break;
            }
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
