package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.authenticator.Base32String;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.muflone.android.django_hotels.Constants;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.ScanType;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.database.dao.TimestampDao;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampEmployee;
import org.fedorahosted.freeotp.Token;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScannerFragment extends Fragment {
    private Context context;
    private View rootLayout;
    private Button enterButton;
    private Button exitButton;
    private ListView timestampEmployeesView;
    private ScanType scanType = ScanType.SCAN_TYPE_UNKNOWN;
    private Api api;
    private ApiData apiData;
    private AppDatabase database;
    private Settings settings;
    private TimestampAdapter timestampAdapter;
    private List<TimestampEmployeeItem> timestampEmployeeList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.api = Singleton.getInstance().api;
        this.apiData = Singleton.getInstance().apiData;
        this.settings = Singleton.getInstance().settings;
        this.database = AppDatabase.getAppDatabase(this.context);

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
        // Load latest timestamps
        this.timestampEmployeeList = new ArrayList<>();
        this.listLatestTimestamps();
        this.timestampAdapter = new TimestampAdapter(this.context,
                R.layout.scanner_timestamps, this.timestampEmployeeList);
        this.timestampEmployeesView.setAdapter(this.timestampAdapter);
        // Clear transmission date on long press
        this.timestampEmployeesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        // Update adapter
                        TimestampEmployeeItem timestampEmployeeItem = timestampEmployeeList.get(position);
                        timestampEmployeeItem.transmission = null;
                        // Update database
                        Timestamp timestamp = database.timestampDao().findById(timestampEmployeeItem.id);
                        timestamp.transmission = null;
                        database.timestampDao().update(timestamp);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void results) {
                        timestampAdapter.notifyDataSetChanged();
                    }
                }.execute();
                return false;
            }
        });
        return this.rootLayout;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    private void loadUI(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup container) {
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
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        // Limit the type of recognized scans to QR Codes
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        // Set scan title
        integrator.setPrompt(this.context.getString(R.string.scan_prompt));
        // Set beep after scan
        integrator.setBeepEnabled(this.settings.getScanBeep());
        // Allow screen rotation during the scan
        integrator.setOrientationLocked(this.settings.getScanOrientationLock());
        // Start scan
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        // Check if the response comes from zxing
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (result != null) {
                if (result.getContents() != null) {
                    Uri uri = Uri.parse(result.getContents());
                    try {
                        // Check for invalid URI
                        if (! uri.getScheme().equals("otpauth") | ! uri.getHost().equals("totp")) {
                            throw new Token.TokenUriInvalidException();
                        }
                        String secret = new String(Base32String.decode(
                                uri.getQueryParameter("secret"))).replaceAll(
                                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                "$1-$2-$3-$4-$5");
                        Log.d("QR", secret);
                        if (this.apiData.contractsGuidMap.containsKey(secret)) {
                            // Valid contract found
                            Contract contract = this.apiData.contractsGuidMap.get(secret);
                            Log.d("QR", contract.employee.firstName + " " + contract.employee.lastName);
                            Toast.makeText(this.context,
                                    contract.employee.firstName + " " + contract.employee.lastName,
                                    Toast.LENGTH_SHORT).show();
                            // Insert new Timestamp in background
                            Timestamp timestamp = new Timestamp(0, contract.id,
                                    this.scanType == ScanType.SCAN_TYPE_ENTER ?
                                            this.apiData.enterDirection.id : this.apiData.exitDirection.id,
                                    this.api.getCurrentDateTime(),
                                    "", null);
                            new AsyncTask<Timestamp, Void, Void>() {
                                @Override
                                protected Void doInBackground(Timestamp... params) {
                                    // Insert new Timestamp
                                    TimestampDao timestampDao = database.timestampDao();
                                    timestampDao.insert(params);
                                    // Reload list
                                    listLatestTimestamps();
                                    return null;
                                }
                            }.execute(timestamp);
                        } else {
                            // Cannot find any contract with the provided GUID
                            Toast.makeText(this.context,
                                    this.context.getString(R.string.message_unknown_employee),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Token.TokenUriInvalidException | Base32String.DecodingException e) {
                        // Unsupported QR Code
                        Toast.makeText(this.context,
                                this.context.getString(R.string.message_unsupported_qrcode),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void listLatestTimestamps() {
        // List the latest timestamps
        new AsyncTask<Long, Void, List<TimestampEmployee>>() {
            @Override
            protected List<TimestampEmployee> doInBackground(Long... params) {
                return database.timestampDao().listByLatest(params[0]);
            }

            @Override
            protected void onPostExecute(List<TimestampEmployee> result) {
                // Reload the timestamps list
                timestampEmployeeList.clear();
                for (TimestampEmployee timestamp : result) {
                    timestampEmployeeList.add(new TimestampEmployeeItem(
                            timestamp.id,
                            String.format("%s %s", timestamp.firstName, timestamp.lastName),
                            timestamp.datetime,
                            timestamp.direction,
                            timestamp.transmission));
                }
                timestampAdapter.notifyDataSetChanged();
            }
        }.execute(Long.valueOf(Constants.LATEST_TIMESTAMPS));
    }

    private class TimestampEmployeeItem {
        final long id;
        final String fullName;
        final Date datetime;
        final String direction;
        Date transmission;

        TimestampEmployeeItem(long id, String fullName, Date datetime,
                              String direction, Date transmission) {
            this.id = id;
            this.fullName = fullName;
            this.datetime = datetime;
            this.direction = direction;
            this.transmission = transmission;
        }
    }

    private class TimestampAdapter extends ArrayAdapter<TimestampEmployeeItem> {
        TimestampAdapter(Context context, int resource, List<TimestampEmployeeItem> objects) {
            super(context, resource, objects);
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            TimestampViewHolder timestampViewHolder;

            if (convertView == null) {
                // Save views for following uses
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.scanner_timestamps, parent, false);
                timestampViewHolder = new TimestampViewHolder();
                timestampViewHolder.employeeView = convertView.findViewById(R.id.employeeView);
                timestampViewHolder.dateView = convertView.findViewById(R.id.dateView);
                timestampViewHolder.timeView = convertView.findViewById(R.id.timeView);
                timestampViewHolder.directionView = convertView.findViewById(R.id.directionView);
                timestampViewHolder.transmissionImage = convertView.findViewById(R.id.transmissionImage);
                convertView.setTag(timestampViewHolder);
            } else {
                // Use cached views
                timestampViewHolder = (TimestampViewHolder) convertView.getTag();
            }
            TimestampEmployeeItem timestampEmployee = getItem(position);
            timestampViewHolder.employeeView.setText(timestampEmployee.fullName);
            timestampViewHolder.dateView.setText(new SimpleDateFormat("yyyy-MM-dd").format(
                    timestampEmployee.datetime));
            timestampViewHolder.timeView.setText(new SimpleDateFormat("HH:mm.ss").format(
                    timestampEmployee.datetime));
            timestampViewHolder.transmissionImage.setImageResource(
                    timestampEmployee.transmission == null ? 0 : R.drawable.ic_timestamp_transmitted);
            timestampViewHolder.directionView.setText(timestampEmployee.direction);
            return convertView;
        }
    }

    private static class TimestampViewHolder {
        // Views holder caching items for TimestampAdapter
        // https://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
        TextView employeeView;
        TextView dateView;
        TextView timeView;
        TextView directionView;
        ImageView transmissionImage;
    }
}
