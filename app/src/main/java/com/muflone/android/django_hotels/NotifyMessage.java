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

package com.muflone.android.django_hotels;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

public class NotifyMessage {
    public static void snackbar(Context context, View rootView, String message, String actionTitle, int duration) {
        // Show a Snackbar with message and action
        Snackbar snackbar = Snackbar.make(rootView, message, duration);
        snackbar.setAction(actionTitle, view -> {
            // This lambda/listener does nothing
        });
        Resources resources = context.getResources();
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(resources.getColor(R.color.colorSnackbarTextColor1));
        textView.setMaxLines(4);
        snackbar.setActionTextColor(resources.getColor(R.color.colorSnackbarTextColor2));
        snackbar.show();
    }

    @SuppressWarnings("unused")
    public static void snackbar(Context context, View rootView, String message, int duration) {
        // Show a Snackbar with the dismiss action
        NotifyMessage.snackbar(context, rootView, message,
                context.getString(R.string.dismiss),
                duration);
    }
}
