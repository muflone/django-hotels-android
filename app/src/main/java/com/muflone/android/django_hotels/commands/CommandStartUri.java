/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2020 Fabio Castelli
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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.muflone.android.django_hotels.database.models.Command;

import org.json.JSONException;

public class CommandStartUri extends CommandBase {
    /**
     * This Command starts an Uri
     *
     * The command must have the following arguments:
     * uri: the Uri to start
     */
    public CommandStartUri(Activity activity, Context context, Command command) {
        super(activity, context, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(this.command.command.getString("uri")));
            this.context.startActivity(intent);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
