package com.muflone.android.django_hotels.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.api.exceptions.InvalidDateTimeException;
import com.muflone.android.django_hotels.api.exceptions.InvalidResponseException;
import com.muflone.android.django_hotels.api.exceptions.NoConnectionException;

public class SyncFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Api api = new Api(getActivity());
        try {
            // Check system date and time
            api.checkDates();
            System.out.println(api.getCurrentTokenCode());
            // Download data from the server
            api.getData();
        } catch (NoConnectionException e) {
            Toast.makeText(getActivity(), getString(R.string.message_no_server_connection), Toast.LENGTH_SHORT).show();
        } catch (InvalidDateTimeException e) {
            Toast.makeText(getActivity(), getString(R.string.message_unmatching_date_time), Toast.LENGTH_SHORT).show();
        } catch (InvalidResponseException e) {
            Toast.makeText(getActivity(), getString(R.string.message_invalid_server_response), Toast.LENGTH_SHORT).show();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sync_fragment, container, false);
    }
}
