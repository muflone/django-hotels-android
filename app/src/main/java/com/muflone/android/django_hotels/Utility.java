package com.muflone.android.django_hotels;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class Utility {
    public static Date getCurrentDateTime(String timezone) {
        // Get current system date and time using the specified timezone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timezone));
        return calendar.getTime();
    }

    public static Date getCurrentDateTime() {
        // Get current system date and time using the default timezone
        return getCurrentDateTime(TimeZone.getDefault().getID());
    }

    public static Date getCurrentDate(String timezone) {
        // Get current system date only using the specified timezone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timezone));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getCurrentDate() {
        // Get current system date only using the default timezone
        return getCurrentDate(TimeZone.getDefault().getID());
    }

    public static Date getCurrentTime(String timezone) {
        // Get current system time using the specified time zone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timezone));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.YEAR, 1970);
        return calendar.getTime();
    }

    public static Date getCurrentTime() {
        // Get current system time only using the default timezone
        return getCurrentTime(TimeZone.getDefault().getID());
    }

    public static int getScreenOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    public static boolean isScreenOrientationPortrait(Context context) {
        return getScreenOrientation(context) == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean isScreenOrientationLandscape(Context context) {
        return getScreenOrientation(context) == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = Objects.requireNonNull(drawable.getConstantState()).newDrawable().mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }

    public static void reloadFragment(AppCompatActivity activity, Fragment fragment) {
        // Reload fragment
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fragmentManager.beginTransaction().detach(fragment).commitNow();
            fragmentManager.beginTransaction().attach(fragment).commitNow();
        } else {
            fragmentManager.beginTransaction().detach(fragment).attach(fragment).commit();
        }
    }
}
