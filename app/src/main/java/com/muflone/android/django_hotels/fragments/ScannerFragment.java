package com.muflone.android.django_hotels.fragments;

import android.arch.persistence.room.Database;
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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.apps.authenticator.Base32String;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.muflone.android.django_hotels.AsyncTaskListener;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.ScanType;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampDirection;
import com.muflone.android.django_hotels.database.tasks.AsyncTaskTimestampInsert;
import com.muflone.android.django_hotels.otp.Token;

import java.util.UUID;

public class ScannerFragment extends Fragment {
    private View rootLayout;
    private Button enterButton;
    private Button exitButton;
    private ScanType scanType = ScanType.SCAN_TYPE_UNKNOWN;
    private ApiData apiData;
    private AppDatabase database;
    private Settings settings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.apiData = Singleton.getInstance().apiData;
        this.settings = Singleton.getInstance().settings;
        this.database = AppDatabase.getAppDatabase(getActivity());

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
        return this.rootLayout;
    }

    protected void loadUI(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup container) {
        // Inflate the layout for this fragment
        this.rootLayout = inflater.inflate(R.layout.scanner_fragment, container, false);
        // Save references
        this.enterButton = this.rootLayout.findViewById(R.id.enterButton);
        this.exitButton = this.rootLayout.findViewById(R.id.exitButton);
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
                            AsyncTaskTimestampInsert task = new AsyncTaskTimestampInsert(this.database, null);
                            Toast.makeText(getActivity(),
                                    contract.employee.firstName + " " + contract.employee.lastName,
                                    Toast.LENGTH_SHORT).show();
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

}
