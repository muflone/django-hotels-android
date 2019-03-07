package com.muflone.android.django_hotels;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

public class NotifyMessage {
    public static void snackbar(View rootView, String message, String actionTitle, int duration) {
        // Show a Snackbar with message and action
        Snackbar snackbar = Snackbar.make(rootView, message, duration);
        snackbar.setAction(actionTitle, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This method does nothing
            }
        });
        Resources resources = NotifyMessage.getActivity(rootView).getResources();
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(resources.getColor(R.color.colorSnackbarTextColor1));
        textView.setMaxLines(4);
        snackbar.setActionTextColor(resources.getColor(R.color.colorSnackbarTextColor2));
        snackbar.show();
    }

    public static void snackbar(View rootView, String message, int duration) {
        // Show a Snackbar with the dismiss action
        NotifyMessage.snackbar(rootView, message,
                NotifyMessage.getActivity(rootView).getString(R.string.snackbar_dismiss),
                duration);
    }

    private static Activity getActivity(View view) {
        // Get the Activity from a View
        return ((Activity) view.getContext());
    }
}