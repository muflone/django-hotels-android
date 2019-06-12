package com.muflone.android.django_hotels;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
                    || ((! listView.isGroupExpanded(index)) && (index == group))) {
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

    public static void copyToClipboard(Context context, String text) {
        // Copy text to the clipboard
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(null, text));
    }

    public static void sendEmail(Context context, String[] recipients, String subject, String body) {
        // Send e-mail to multiple recipients
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.setType("message/rfc822");
        context.startActivity(intent);
    }

    public static String left(String string, int length) {
        // Extract leftmost part of a string
        return string == null ? null : (string.substring(
                0,
                string.length() >= length ? length : string.length()));
    }

    public static String right(String string, int length) {
        // Extract rightmost part of a string
        return string == null ? null : (string.substring(
                string.length() >= length ? string.length() - length : 0));
    }

    public static void createShortcutIcon(Activity activity, String title, int width, int height,
                                          @Nullable String base64Icon, Class<?> klass) {
        // Add a shortcut icon to the home
        Intent shortcutIntent;
        shortcutIntent = new Intent(activity, klass);

        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        // Get shortcut title
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        if (base64Icon != null) {
            // Use custom bitmap icon
            byte[] icon = Base64.decode(base64Icon, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(icon, 0, icon.length);
            if (width == 0) {
                width = bitmap.getWidth();
            }
            if (height == 0) {
                height = bitmap.getHeight();
            }
            // Resize bitmap if needed
            if (width != bitmap.getWidth() | height != bitmap.getHeight()) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
        } else {
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(),
                            R.mipmap.ic_launcher));
        }
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        activity.sendBroadcast(intent);
    }
}
