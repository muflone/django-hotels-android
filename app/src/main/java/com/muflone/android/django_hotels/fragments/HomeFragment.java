package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.muflone.android.django_hotels.FragmentLoader;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.activities.MainActivity;

public class HomeFragment extends Fragment {
    private Context context;
    private View rootLayout;
    private ImageView imageSectionScanner;
    private TextView textViewScanner;
    private ImageView imageSectionStructures;
    private TextView textViewStructures;
    private ImageView imageSectionExtra;
    private TextView textViewExtra;
    private ImageView imageSectionSync;
    private TextView textViewSync;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Initialize UI
        this.loadUI(inflater, container);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fragmentName;
                if (view == imageSectionScanner || view == textViewScanner) {
                    fragmentName = FragmentLoader.FRAGMENT_SCANNER;
                } else if (view == imageSectionStructures || view == textViewStructures) {
                    fragmentName = FragmentLoader.FRAGMENT_STRUCTURES;
                } else if (view == imageSectionExtra || view == textViewExtra) {
                    fragmentName = FragmentLoader.FRAGMENT_EXTRA;
                } else if (view == imageSectionSync || view == textViewSync) {
                    fragmentName = FragmentLoader.FRAGMENT_SYNC;
                } else {
                    fragmentName = null;
                }
                FragmentLoader.loadFragment((MainActivity) getActivity(), R.id.fragment_container, fragmentName);
            }
        };
        this.imageSectionScanner.setOnClickListener(clickListener);
        this.textViewScanner.setOnClickListener(clickListener);
        this.imageSectionStructures.setOnClickListener(clickListener);
        this.textViewStructures.setOnClickListener(clickListener);
        this.imageSectionExtra.setOnClickListener(clickListener);
        this.textViewExtra.setOnClickListener(clickListener);
        this.imageSectionSync.setOnClickListener(clickListener);
        this.textViewSync.setOnClickListener(clickListener);

        return this.rootLayout;
    }

    private void loadUI(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup container) {
        // Inflate the layout for this fragment
        this.rootLayout = inflater.inflate(R.layout.home_fragment, container, false);
        // Save references
        this.imageSectionScanner = this.rootLayout.findViewById(R.id.imageSectionScanner);
        this.textViewScanner = this.rootLayout.findViewById(R.id.textViewScanner);
        this.imageSectionStructures = this.rootLayout.findViewById(R.id.imageSectionStructures);
        this.textViewStructures = this.rootLayout.findViewById(R.id.textViewStructures);
        this.imageSectionExtra = this.rootLayout.findViewById(R.id.imageSectionExtra);
        this.textViewExtra = this.rootLayout.findViewById(R.id.textViewExtra);
        this.imageSectionSync = this.rootLayout.findViewById(R.id.imageSectionSync);
        this.textViewSync = this.rootLayout.findViewById(R.id.textViewSync);
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }
}
