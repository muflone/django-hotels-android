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
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.activities.MainActivity;
import com.muflone.android.django_hotels.commands.CommandConstants;

import java.util.Objects;

public class HomeFragment extends Fragment {
    private View rootLayout;
    private ImageView imageSectionScanner;
    private TextView textViewScanner;
    private ImageView imageSectionStructures;
    private TextView textViewStructures;
    private ImageView imageSectionExtra;
    private TextView textViewExtra;
    private ImageView imageSectionReports;
    private TextView textViewReports;
    private ImageView imageSectionSync;
    private TextView textViewSync;
    private ImageView imageSectionAbout;
    private TextView textViewAbout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Execute START MAIN BEGIN commands
        Singleton.getInstance().commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_MAIN_BEGIN);
        // Initialize UI
        this.loadUI(inflater, Objects.requireNonNull(container));
        View.OnClickListener clickListener = view -> {
            String fragmentName;
            if (view == this.imageSectionScanner || view == this.textViewScanner) {
                fragmentName = FragmentLoader.FRAGMENT_SCANNER;
            } else if (view == this.imageSectionStructures || view == this.textViewStructures) {
                fragmentName = FragmentLoader.FRAGMENT_STRUCTURES;
            } else if (view == this.imageSectionExtra || view == this.textViewExtra) {
                fragmentName = FragmentLoader.FRAGMENT_EXTRA;
            } else if (view == this.imageSectionReports || view == this.textViewReports) {
                fragmentName = FragmentLoader.FRAGMENT_REPORTS;
            } else if (view == this.imageSectionSync || view == this.textViewSync) {
                fragmentName = FragmentLoader.FRAGMENT_SYNC;
            } else if (view == this.imageSectionAbout || view == this.textViewAbout) {
                fragmentName = FragmentLoader.FRAGMENT_ABOUT;
            } else {
                fragmentName = null;
            }
            FragmentLoader.loadFragment((MainActivity) getActivity(), R.id.fragment_container, Objects.requireNonNull(fragmentName));
        };
        this.imageSectionScanner.setOnClickListener(clickListener);
        this.textViewScanner.setOnClickListener(clickListener);
        this.imageSectionStructures.setOnClickListener(clickListener);
        this.textViewStructures.setOnClickListener(clickListener);
        this.imageSectionExtra.setOnClickListener(clickListener);
        this.textViewExtra.setOnClickListener(clickListener);
        this.imageSectionReports.setOnClickListener(clickListener);
        this.textViewReports.setOnClickListener(clickListener);
        this.imageSectionSync.setOnClickListener(clickListener);
        this.textViewSync.setOnClickListener(clickListener);
        this.imageSectionAbout.setOnClickListener(clickListener);
        this.textViewAbout.setOnClickListener(clickListener);
        // Execute START MAIN END commands
        Singleton.getInstance().commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_MAIN_END);
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
        this.imageSectionReports = this.rootLayout.findViewById(R.id.imageSectionReports);
        this.textViewReports = this.rootLayout.findViewById(R.id.textViewReports);
        this.imageSectionSync = this.rootLayout.findViewById(R.id.imageSectionSync);
        this.textViewSync = this.rootLayout.findViewById(R.id.textViewSync);
        this.imageSectionAbout = this.rootLayout.findViewById(R.id.imageSectionAbout);
        this.textViewAbout = this.rootLayout.findViewById(R.id.textViewAbout);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
