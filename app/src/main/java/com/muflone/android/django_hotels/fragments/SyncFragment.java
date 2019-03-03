package com.muflone.android.django_hotels.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.muflone.android.django_hotels.Api;
import com.muflone.android.django_hotels.R;


public class SyncFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Api api = new Api(getActivity());
        // Check system date and time
        if (!api.checkDates()) {
            Toast.makeText(getActivity(), getString(R.string.message_unmatching_date_time), Toast.LENGTH_SHORT).show();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sync_fragment, container, false);
    }
}
