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

package com.muflone.android.django_hotels;

public class Constants {
    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "hotels.sqlite";
    public static final int LATEST_TIMESTAMPS = 30;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 5001;
    public static final int SYNC_CONNECT_TIMEOUT_DEFAULT = 30 * 1000;
    public static final int SYNC_READ_TIMEOUT_DEFAULT = 60 * 1000;
    public static final long EXTRAS_SERVICE_ID = -1;
}
