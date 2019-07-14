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
