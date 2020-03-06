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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.exceptions.InvalidServerStatusException;
import com.muflone.android.django_hotels.api.exceptions.RetransmittedActivityException;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.tasks.TaskSync;
import com.muflone.android.django_hotels.tasks.TaskListenerInterface;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.api.exceptions.InvalidDateTimeException;
import com.muflone.android.django_hotels.api.exceptions.InvalidResponseException;
import com.muflone.android.django_hotels.api.exceptions.NoConnectionException;
import com.muflone.android.django_hotels.api.exceptions.NoDownloadException;
import com.muflone.android.django_hotels.tasks.TaskResult;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class SyncFragment extends Fragment {
    private Context context;
    private NumberProgressBar progressBar;
    private ProgressBar progressBar2;
    private TextView progressView;
    private TextView errorMessage;
    private TextView errorMessageDetails;
    private ImageView errorView;
    private final List<String> progressPhases = new ArrayList<>();
    private final String TAG = getClass().getSimpleName();
    private final Singleton singleton = Singleton.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Execute START SYNC BEGIN commands
        this.singleton.commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_SYNC_BEGIN);

        View rootView = inflater.inflate(R.layout.sync_fragment, container, false);
        this.progressBar = rootView.findViewById(R.id.progressBar);
        this.progressBar2 = rootView.findViewById(R.id.progressBar2);
        this.progressView = rootView.findViewById(R.id.progressView);
        this.errorMessage = rootView.findViewById(R.id.errorMessage);
        this.errorMessage.setText("");
        this.errorView = rootView.findViewById(R.id.errorView);
        this.errorView.setVisibility(View.INVISIBLE);
        this.errorMessageDetails = rootView.findViewById(R.id.errorMessageDetails);
        this.errorMessageDetails.setVisibility(View.INVISIBLE);
        this.progressPhases.add(this.context.getString(R.string.sync_step_server_status));
        this.progressPhases.add(this.context.getString(R.string.sync_step_date_time));
        this.progressPhases.add(this.context.getString(R.string.sync_step_timestamps_transmission));
        this.progressPhases.add(this.context.getString(R.string.sync_step_activities_transmission));
        this.progressPhases.add(this.context.getString(R.string.sync_step_extras_transmission));
        this.progressPhases.add(this.context.getString(R.string.sync_step_get_data));
        this.progressPhases.add(this.context.getString(R.string.sync_step_saving_data));
        this.progressPhases.add(this.context.getString(R.string.sync_step_completed));
        this.progressBar.setMax(this.progressPhases.size());

        // Execute START SYNC END commands
        this.singleton.commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_SYNC_END);
        // Download data asynchronously from the server
        TaskSync task = new TaskSync(
                this.context,
                this.singleton.api,
                this.progressPhases.size(),
                new TaskListenerInterface() {
                    @Override
                    public void onSuccess(TaskResult result) {
                        // Reload data from database
                        SyncFragment.this.singleton.database.reload(context, new TaskListenerInterface() {
                            @Override
                            public void onSuccess(TaskResult result) {
                                // Complete synchronization only after the data was reloaded from DB
                                errorView.setImageDrawable(
                                        context.getResources().getDrawable(R.drawable.ic_check_ok));
                                progressBar2.setVisibility(View.INVISIBLE);
                                errorView.setVisibility(View.VISIBLE);
                                // Update options menu if available
                                if (getActivity() != null) {
                                    if (singleton.apiData.structuresMap.size() > 0) {
                                        // Select the latest chosen structure or the first available
                                        SortedSet<Structure> sortedStructures = new TreeSet<>(singleton.apiData.structuresMap.values());
                                        long latestStructureSelected = (
                                                SyncFragment.this.singleton.selectedStructure != null ?
                                                SyncFragment.this.singleton.selectedStructure.id :
                                                0);
                                        SyncFragment.this.singleton.selectedStructure = (
                                                singleton.apiData.structuresMap.containsKey(latestStructureSelected) ?
                                                singleton.apiData.structuresMap.get(latestStructureSelected) :
                                                sortedStructures.first());
                                    } else {
                                        // No available structures
                                        SyncFragment.this.singleton.selectedStructure = null;
                                    }
                                    getActivity().invalidateOptionsMenu();
                                }
                                // Execute SYNC END commands
                                SyncFragment.this.singleton.commandFactory.executeCommands(
                                        SyncFragment.this.getActivity(),
                                        SyncFragment.this.getContext(),
                                        CommandConstants.CONTEXT_SYNC_END);
                            }

                            @Override
                            public void onFailure(Exception exception) {
                            }

                            @Override
                            public void onProgress(int step, int total) {
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        if (exception instanceof NoConnectionException) {
                            errorMessage.setText(R.string.sync_error_no_server_connection);
                            errorView.setImageDrawable(
                                    context.getResources().getDrawable(R.drawable.ic_error_signal));
                        } else if (exception instanceof InvalidDateTimeException) {
                            errorMessage.setText(R.string.sync_error_invalid_date_time);
                            errorView.setImageDrawable(
                                    context.getResources().getDrawable(R.drawable.ic_error_date));
                        } else if (exception instanceof InvalidServerStatusException) {
                            errorMessage.setText(R.string.sync_error_invalid_server_status);
                            errorView.setImageDrawable(
                                    context.getResources().getDrawable(R.drawable.ic_error_cloud_off));
                        } else if (exception instanceof InvalidResponseException) {
                            errorMessage.setText(R.string.sync_error_invalid_server_response);
                            errorView.setImageDrawable(
                                    context.getResources().getDrawable(R.drawable.ic_error_generic));
                        } else if (exception instanceof RetransmittedActivityException) {
                            errorMessage.setText(R.string.sync_error_retransmitted_response);
                            errorView.setImageDrawable(
                                    context.getResources().getDrawable(R.drawable.ic_error_generic));
                        } else if (exception instanceof NoDownloadException) {
                            errorMessage.setText(R.string.sync_error_unable_to_download);
                            errorView.setImageDrawable(
                                    context.getResources().getDrawable(R.drawable.ic_error_generic));
                        }
                        progressBar2.setVisibility(View.INVISIBLE);
                        errorView.setVisibility(View.VISIBLE);
                        // Show error details
                        String message = exception.getMessage();
                        if (message != null)
                        {
                            Log.w(TAG, message);
                            errorMessageDetails.setText(message);
                            errorMessageDetails.setVisibility(View.VISIBLE);
                        }
                        // Execute SYNC FAIL commands
                        SyncFragment.this.singleton.commandFactory.executeCommands(
                                SyncFragment.this.getActivity(),
                                SyncFragment.this.getContext(),
                                CommandConstants.CONTEXT_SYNC_FAIL);
                    }

                    @Override
                    public void onProgress(int step, int total) {
                        // Execute SYNC PROGRESS commands
                        SyncFragment.this.singleton.commandFactory.executeCommands(
                                SyncFragment.this.getActivity(),
                                SyncFragment.this.getContext(),
                                CommandConstants.CONTEXT_SYNC_PROGRESS);
                        // Update progress bar
                        if (step <= total) {
                            progressView.post(new Runnable() {
                                private int step;

                                @SuppressWarnings("WeakerAccess")
                                public Runnable init(int step) {
                                    this.step = step;
                                    return (this);
                                }

                                @Override
                                public void run() {
                                    progressBar.setProgress(this.step);
                                    progressView.setText(progressPhases.get(this.step - 1));
                                }
                            }.init(step));
                        }
                    }
                });
        task.execute();
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }
}
