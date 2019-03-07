package com.muflone.android.django_hotels.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muflone.android.django_hotels.NotifyMessage;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.api.exceptions.InvalidDateTimeException;
import com.muflone.android.django_hotels.api.exceptions.InvalidResponseException;
import com.muflone.android.django_hotels.api.exceptions.NoConnectionException;
import com.muflone.android.django_hotels.api.exceptions.NoDownloadExeception;
import com.muflone.android.django_hotels.api.results.GetDataResults;

import java.text.ParseException;

public class SyncFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootLayout = getActivity().findViewById(R.id.drawer_layout);
        Api api = new Api(getActivity());
        GetDataResults results;
        try {
            // Check system date and time
            api.checkDates();
            System.out.println(api.getCurrentTokenCode());
            // Download data from the server
            results = api.getData();
            NotifyMessage.snackbar(rootLayout,
                    getString(R.string.message_established_connection),
                    Snackbar.LENGTH_INDEFINITE);
        } catch (NoConnectionException e) {
            NotifyMessage.snackbar(rootLayout,
                    getString(R.string.message_no_server_connection),
                    Snackbar.LENGTH_INDEFINITE);
        } catch (InvalidDateTimeException e) {
            NotifyMessage.snackbar(rootLayout,
                    getString(R.string.message_invalid_date_time),
                    Snackbar.LENGTH_INDEFINITE);
        } catch (InvalidResponseException | ParseException e) {
            NotifyMessage.snackbar(rootLayout,
                    getString(R.string.message_invalid_server_response),
                    Snackbar.LENGTH_INDEFINITE);
        } catch (NoDownloadExeception e) {
            NotifyMessage.snackbar(rootLayout,
                    getString(R.string.message_unable_to_download),
                    Snackbar.LENGTH_INDEFINITE);
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sync_fragment, container, false);
    }
}
