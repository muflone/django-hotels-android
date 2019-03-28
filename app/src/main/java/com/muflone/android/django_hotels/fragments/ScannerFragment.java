package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.authenticator.Base32String;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.ScanType;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.tasks.AsyncTaskTimestampInsert;
import com.muflone.android.django_hotels.otp.Token;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ScannerFragment extends Fragment {
    private View rootLayout;
    private Button enterButton;
    private Button exitButton;
    private ListView timestampEmployeesView;
    private ScanType scanType = ScanType.SCAN_TYPE_UNKNOWN;
    private ApiData apiData;
    private AppDatabase database;
    private Settings settings;
    private List<TimestampEmployee> timestampEmployeeList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.apiData = Singleton.getInstance().apiData;
        this.settings = Singleton.getInstance().settings;
        this.database = AppDatabase.getAppDatabase(getActivity());

        this.timestampEmployeeList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            this.timestampEmployeeList.add(new TimestampEmployee("prova ciao " + i,
                    Utility.getCurrentDate(this.settings.getTimeZone()),
                    Utility.getCurrentTime(this.settings.getTimeZone())));
        }

        // Initialize UI
        this.loadUI(inflater, container);

        this.enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQRScanner(true);
            }
        });
        this.exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQRScanner(false);
            }
        });
        this.timestampEmployeesView.setAdapter(new TimestampAdapter(getActivity(),
                R.layout.scanner_timestamps, this.timestampEmployeeList));
        return this.rootLayout;
    }

    protected void loadUI(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup container) {
        // Inflate the layout for this fragment
        this.rootLayout = inflater.inflate(R.layout.scanner_fragment, container, false);
        // Save references
        this.enterButton = this.rootLayout.findViewById(R.id.enterButton);
        this.exitButton = this.rootLayout.findViewById(R.id.exitButton);
        this.timestampEmployeesView = this.rootLayout.findViewById(R.id.timestampEmployeesView);
    }

    private void startQRScanner(boolean enter) {
        // Initiate scan via zxing
        this.scanType = enter ? ScanType.SCAN_TYPE_ENTER : ScanType.SCAN_TYPE_EXIT;
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        // Check if the response comes from zxing
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (result != null) {
                if (result.getContents() != null) {
                    Uri uri = Uri.parse(result.getContents());

                    Log.d("QR", this.scanType == ScanType.SCAN_TYPE_ENTER ? "enter" : "exit");
                    Log.d("QR", uri.toString());
                    try {
                        // Decode key and parse as GUID
                        Token token = new Token(uri);
                        // Check for invalid URI
                        if (! uri.getScheme().equals("otpauth") | ! uri.getHost().equals("totp")) {
                            throw new Token.TokenUriInvalidException();
                        }
                        String secret = new String(Base32String.decode(
                                uri.getQueryParameter("secret"))).replaceAll(
                                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                "$1-$2-$3-$4-$5");
                        UUID guid = UUID.fromString(secret);
                        Log.d("QR", secret);
                        if (this.apiData.contractsGuidMap.containsKey(secret)) {
                            // Valid contract found
                            Contract contract = this.apiData.contractsGuidMap.get(secret);
                            Log.d("QR", contract.employee.firstName + " " + contract.employee.lastName);
                            Toast.makeText(getActivity(),
                                    contract.employee.firstName + " " + contract.employee.lastName,
                                    Toast.LENGTH_SHORT).show();
                            AsyncTaskTimestampInsert task = new AsyncTaskTimestampInsert(this.database, null);
                            task.execute(new Timestamp(contract.id,
                                    this.scanType == ScanType.SCAN_TYPE_ENTER ?
                                            this.apiData.enterDirection.id : this.apiData.exitDirection.id,
                                    Utility.getCurrentDate(this.settings.getTimeZone()),
                                    Utility.getCurrentTime(this.settings.getTimeZone()),
                                    ""));

                        } else {
                            // Cannot find any contract with the provided GUID
                            Toast.makeText(getActivity(),
                                    getString(R.string.message_unnown_employee),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Token.TokenUriInvalidException | Base32String.DecodingException e) {
                        // Unsupported QR Code
                        Toast.makeText(getActivity(),
                                getString(R.string.message_unsupported_qrcode),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class TimestampEmployee {
        public String fullName;
        public Date date;
        public Date time;

        public TimestampEmployee(String fullName, Date date, Date time) {
            this.fullName = fullName;
            this.date = date;
            this.time = time;
        }
    }

    private class TimestampAdapter extends ArrayAdapter<TimestampEmployee> {
        public TimestampAdapter(Context context, int resource, List<TimestampEmployee> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.scanner_timestamps, null);
            TextView employeeView = convertView.findViewById(R.id.employeeView);
            TextView dateView = convertView.findViewById(R.id.dateView);
            TextView timeView = convertView.findViewById(R.id.timeView);
            TimestampEmployee timestampEmployee = getItem(position);
            employeeView.setText(timestampEmployee.fullName);
            dateView.setText(new SimpleDateFormat("yyyy-MM-dd").format(timestampEmployee.date));
            timeView.setText(new SimpleDateFormat("HH:mm.ss").format(timestampEmployee.time));
            return convertView;
        }
    }
}
