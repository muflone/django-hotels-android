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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

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

    public static void sleep(long milliseconds) {
        // Pause for some time
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
        }
    }

    public static void setExpandableListViewHeight(ExpandableListView listView, int group, boolean standardHeight) {
        // Set the ListView height
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        int dividerHeight = listView.getDividerHeight();
        int groupsCount = listAdapter.getGroupCount();
        // Get standard group and item height
        int standardGroupHeight = 0;
        int standardItemHeight = 0;
        if (standardHeight && groupsCount > 0) {
            View groupItem = listAdapter.getGroupView(0, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            standardGroupHeight = groupItem.getMeasuredHeight();
            View listItem = listAdapter.getChildView(0, 0, false, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            standardItemHeight = listItem.getMeasuredHeight();
        }
        for (int index = 0; index < groupsCount; index++) {
            // Use the same standard height for every group
            if (standardHeight) {
                totalHeight += standardGroupHeight;
            } else {
                // Do not use standard height (slower)
                // Need to cycle on every group to get its real height
                View groupItem = listAdapter.getGroupView(index, false, null, listView);
                groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += groupItem.getMeasuredHeight();
            }

            if (((listView.isGroupExpanded(index)) && (index != group))
                    || ((!listView.isGroupExpanded(index)) && (index == group))) {
                int childrenCount = listAdapter.getChildrenCount(index);
                if (standardHeight) {
                    // Use the same standard height for every item in the group
                    totalHeight += standardItemHeight * childrenCount;
                } else {
                    // Do not use standard height (slower)
                    // Need to cycle on every item to get its real height
                    for (int j = 0; j < childrenCount; j++) {
                        View listItem = listAdapter.getChildView(index, j, false, null,
                                listView);
                        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                        totalHeight += listItem.getMeasuredHeight();
                    }
                }
                // Add Divider Height
                totalHeight += dividerHeight * (childrenCount - 1);
            }
        }
        // Add Divider Height
        totalHeight += dividerHeight * (groupsCount - 1);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (dividerHeight * (groupsCount - 1));
        params.height = height < 10 ? 200 : height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
