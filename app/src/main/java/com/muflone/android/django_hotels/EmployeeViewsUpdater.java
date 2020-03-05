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

import android.widget.ImageView;
import android.widget.TextView;

import com.muflone.android.django_hotels.database.models.Employee;

public class EmployeeViewsUpdater {
    private final TextView employeeIdView;
    private final TextView employeeFirstNameView;
    private final TextView employeeLastNameView;
    private final ImageView employeeGenderImageView;

    public EmployeeViewsUpdater(TextView employeeIdView, TextView employeeFirstNameView,
                                TextView employeeLastNameView, ImageView employeeGenderImageView) {
        this.employeeIdView = employeeIdView;
        this.employeeFirstNameView = employeeFirstNameView;
        this.employeeLastNameView = employeeLastNameView;
        this.employeeGenderImageView = employeeGenderImageView;
    }

    public void updateViews(Employee employee) {
        // Update views
        this.employeeIdView.setText(String.valueOf(employee.id));
        this.employeeFirstNameView.setText(employee.firstName);
        this.employeeLastNameView.setText(employee.lastName);
        int genderResourceId;
        switch (employee.gender) {
            case "male":
                genderResourceId = R.drawable.ic_gender_male;
                break;
            case "female":
                genderResourceId = R.drawable.ic_gender_female;
                break;
            default:
                genderResourceId = R.drawable.ic_gender_unknown;
                break;
        }
        this.employeeGenderImageView.setImageResource(genderResourceId);
    }
}
