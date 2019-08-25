package com.muflone.android.django_hotels;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Utility {
    @SuppressWarnings("WeakerAccess")
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

    @SuppressWarnings("WeakerAccess")
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

    @SuppressWarnings("WeakerAccess")
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

    @SuppressWarnings("WeakerAccess")
    public static int getScreenOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    @SuppressWarnings("unused")
    public static boolean isScreenOrientationPortrait(Context context) {
        return getScreenOrientation(context) == Configuration.ORIENTATION_PORTRAIT;
    }

    @SuppressWarnings("unused")
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

    public static void sendEmail(Context context, String[] recipients, String subject, String body,
                                 Uri[] attachments) {
        // Send e-mail to multiple recipients
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.setType("message/rfc822");
        // Add attachments
        if (attachments != null) {
            for (Uri uri : attachments) {
                if (uri != null) {
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                }
            }
        }
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
                                          @Nullable String base64Icon,
                                          @SuppressWarnings("SpellCheckingInspection") Class<?> klass) {
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

    @SuppressWarnings("UnusedReturnValue")
    public static boolean requestWriteStoragePermission(Activity activity) {
        boolean hasPermission = (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (! hasPermission) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            return false;
        } else {
            return true;
        }
    }

    public static String backupDatabase(Context context, String databasePath, int version) {
        // Backup database to external storage
        String result = null;
        File destinationDirectory = new File(
                context.getExternalFilesDir(null) +
                        File.separator +
                        "backups");
        // Create missing destination directory
        if (! destinationDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            destinationDirectory.mkdir();
        }
        File sourceFile = new File(databasePath);
        //noinspection SpellCheckingInspection
        File destinationFile = new File(
                destinationDirectory.getAbsoluteFile() +
                        File.separator +
                        String.format(Locale.ROOT, "%s_v%d.sqlite",
                                new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()),
                                version));
        try {
            FileInputStream inStream = new FileInputStream(sourceFile);
            FileOutputStream outStream = new FileOutputStream(destinationFile);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
            result = destinationFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void setSupportActionBarTitle(AppCompatActivity activity, String title) {
        // Update activity support ActionBar title
        if (title != null) {
            //noinspection ConstantConditions
            activity.getSupportActionBar().setTitle(String.format("%s - %s",
                    activity.getString(R.string.app_name),
                    title));
        }
    }

    public static String formatElapsedTime(long seconds, String format, boolean showSeconds) {
        // Format elapsed time
        String result;
        long hours = TimeUnit.SECONDS.toHours(seconds);
        seconds -= TimeUnit.HOURS.toSeconds(hours);

        long minutes = TimeUnit.SECONDS.toMinutes (seconds);
        seconds -= TimeUnit.MINUTES.toSeconds(minutes);

        if (showSeconds) {
            result = String.format (format == null ? "%02d:%02d:%02d" : format, hours, minutes, seconds);
        } else {
            result = String.format (format == null ? "%02d:%02d" : format, hours, minutes);
        }
        return hours != 0 | minutes != 0 | seconds != 0 ? result : "";
    }

    @SuppressWarnings("unchecked")
    public static List sortHashtableKeys(Hashtable table, boolean reverse) {
        // Returns a sorted list of keys in a Hashtable
        List result = Collections.list(table.keys());
        Collections.sort(result);
        if (reverse) {
            Collections.reverse(result);
        }
        return result;
    }
}
