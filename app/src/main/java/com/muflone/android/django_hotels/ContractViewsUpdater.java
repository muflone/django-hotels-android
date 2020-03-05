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

import android.widget.TextView;

import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractType;

import java.util.Objects;

public class ContractViewsUpdater {
    private final Singleton singleton = Singleton.getInstance();
    private final TextView contractIdView;
    private final TextView contractCompanyView;
    private final TextView contractStatusView;
    private final TextView contractTypeView;
    private final TextView contractDailyHoursView;
    private final TextView contractWeeklyHoursView;
    private final TextView contractStartDateView;
    private final TextView contractEndDateView;

    public ContractViewsUpdater(TextView contractIdView, TextView contractCompanyView,
                                TextView contractStatusView, TextView contractTypeView,
                                TextView contractDailyHoursView, TextView contractWeeklyHoursView,
                                TextView contractStartDateView, TextView contractEndDateView) {
        this.contractIdView = contractIdView;
        this.contractCompanyView = contractCompanyView;
        this.contractStatusView = contractStatusView;
        this.contractTypeView = contractTypeView;
        this.contractDailyHoursView = contractDailyHoursView;
        this.contractWeeklyHoursView = contractWeeklyHoursView;
        this.contractStartDateView = contractStartDateView;
        this.contractEndDateView = contractEndDateView;
    }

    public void updateViews(Contract contract) {
        // Update views
        this.contractIdView.setText(String.valueOf(contract.id));
        this.contractCompanyView.setText(Objects.requireNonNull(this.singleton.apiData.companiesMap.get(contract.companyId)).name);
        // Add contract info
        ContractType contractType = Objects.requireNonNull(this.singleton.apiData.contractTypeMap.get(contract.contractTypeId));
        this.contractStatusView.setText(contract.enabled ? R.string.enabled : R.string.disabled);
        this.contractTypeView.setText(contractType.name);
        this.contractDailyHoursView.setText(String.valueOf(contractType.dailyHours));
        this.contractWeeklyHoursView.setText(String.valueOf(contractType.weeklyHours));
        // Add contract dates
        this.contractStartDateView.setText(this.singleton.defaultDateFormatter.format(contract.startDate));
        this.contractEndDateView.setText(this.singleton.defaultDateFormatter.format(contract.endDate));
    }
}
