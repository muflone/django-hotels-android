package com.muflone.android.django_hotels;

import android.widget.TextView;

import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.ContractType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        this.contractStartDateView.setText(formatter.format(contract.startDate));
        this.contractEndDateView.setText(formatter.format(contract.endDate));
    }
}
