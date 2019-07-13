package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
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
import com.muflone.android.django_hotels.TimestampEmployeeItem;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.tasks.TaskInsertTimestamp;
import com.muflone.android.django_hotels.tasks.TaskListenerInterface;
import com.muflone.android.django_hotels.tasks.TaskResult;
import com.muflone.android.django_hotels.tasks.TaskScannerListLatestTimestamps;
import com.muflone.android.django_hotels.tasks.TaskTimestampUnsetTransmission;

import org.fedorahosted.freeotp.Token;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScannerFragment extends Fragment {
    private final Singleton singleton = Singleton.getInstance();
    private Context context;
    private View rootLayout;
    private Button enterButton;
    private Button exitButton;
    private ListView timestampEmployeesView;
    private ScanType scanType = ScanType.SCAN_TYPE_UNKNOWN;
    private ApiData apiData;
    private Settings settings;
    private TimestampAdapter timestampAdapter;
    private List<TimestampEmployeeItem> timestampEmployeeList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Execute START SCANNER BEGIN commands
        this.singleton.commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_SCANNER_BEGIN);

        this.apiData = this.singleton.apiData;
        this.settings = this.singleton.settings;

        // Initialize UI
        this.loadUI(inflater, Objects.requireNonNull(container));

        this.enterButton.setOnClickListener(view -> startQRScanner(true));
        this.exitButton.setOnClickListener(view -> startQRScanner(false));
        // Load latest timestamps
        this.timestampEmployeeList = new ArrayList<>();
        this.timestampAdapter = new TimestampAdapter(this.context,
                R.layout.scanner_timestamps, this.timestampEmployeeList);
        this.timestampEmployeesView.setAdapter(this.timestampAdapter);
        this.listLatestTimestamps();
        // Clear transmission date on long press
        this.timestampEmployeesView.setOnItemLongClickListener((adapterView, view, position, l) -> {
            new TaskTimestampUnsetTransmission(this.context, this.timestampEmployeeList,
                    this.timestampAdapter).execute(position);
            return false;
        });
        // Execute START SCANNER END commands
        this.singleton.commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_SCANNER_END);
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
        TextClock dayClock = this.rootLayout.findViewById(R.id.dayClock);
        TextClock dateClock = this.rootLayout.findViewById(R.id.dateClock);
        TextClock timeClock = this.rootLayout.findViewById(R.id.timeClock);
        // Set clock/date format based on server settings
        dayClock.setFormat24Hour(this.settings.getString(CommandConstants.SETTING_SCANNER_CURRENT_DAY_FORMAT, "EEEE"));
        dateClock.setFormat24Hour(this.settings.getString(CommandConstants.SETTING_SCANNER_CURRENT_DATE_FORMAT, "yyyy/MM/dd"));
        timeClock.setFormat24Hour(this.settings.getString(CommandConstants.SETTING_SCANNER_CURRENT_TIME_FORMAT, "HH:mm.ss"));
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
                        if (! Objects.requireNonNull(uri.getScheme()).equals("otpauth") | ! Objects.requireNonNull(uri.getHost()).equals("totp")) {
                            throw new Token.TokenUriInvalidException();
                        }
                        String secret = new String(Base32String.decode(
                                uri.getQueryParameter("secret"))).replaceAll(
                                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                "$1-$2-$3-$4-$5");
                        if (this.apiData.contractsGuidMap.containsKey(secret)) {
                            // Valid contract found
                            Contract contract = Objects.requireNonNull(this.apiData.contractsGuidMap.get(secret));
                            Toast.makeText(this.context,
                                    contract.employee.firstName + " " + contract.employee.lastName,
                                    Toast.LENGTH_SHORT).show();
                            // Insert new Timestamp in background
                            Timestamp timestamp = new Timestamp(0, contract.id,
                                    this.scanType == ScanType.SCAN_TYPE_ENTER ?
                                            this.apiData.enterDirection.id : this.apiData.exitDirection.id,
                                    Utility.getCurrentDateTime(),
                                    "", null);
                            new TaskInsertTimestamp(new TaskListenerInterface() {
                                @Override
                                public void onSuccess(TaskResult result) {
                                    // Reload list
                                    listLatestTimestamps();
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                }

                                @Override
                                public void onProgress(int step, int total) {
                                }
                            }).execute(timestamp);
                        } else {
                            // Cannot find any contract with the provided GUID
                            Toast.makeText(this.context,
                                    this.context.getString(R.string.scan_unknown_employee),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Token.TokenUriInvalidException | Base32String.DecodingException exception) {
                        // Unsupported QR Code
                        Toast.makeText(this.context,
                                this.context.getString(R.string.scan_unsupported_qrcode),
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
        new TaskScannerListLatestTimestamps(this.timestampEmployeeList,
                this.timestampAdapter).execute(Long.valueOf(Constants.LATEST_TIMESTAMPS));
    }

    public class TimestampAdapter extends ArrayAdapter<TimestampEmployeeItem> {
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
            TimestampEmployeeItem timestampEmployee = Objects.requireNonNull(getItem(position));
            timestampViewHolder.employeeView.setText(timestampEmployee.fullName);
            timestampViewHolder.dateView.setText(new SimpleDateFormat(singleton.settings.getString(
                    CommandConstants.SETTING_SCANNER_TIMESTAMPS_FORMAT_DATE, "yyyy-MM-dd")).format(
                            timestampEmployee.datetime));
            timestampViewHolder.timeView.setText(new SimpleDateFormat(singleton.settings.getString(
                    CommandConstants.SETTING_SCANNER_TIMESTAMPS_FORMAT_TIME, "HH:mm.ss")).format(
                            timestampEmployee.datetime));
            timestampViewHolder.transmissionImage.setImageResource(
                    timestampEmployee.transmission == null ?
                            R.drawable.ic_timestamp_untransmitted :
                            R.drawable.ic_timestamp_transmitted);
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
