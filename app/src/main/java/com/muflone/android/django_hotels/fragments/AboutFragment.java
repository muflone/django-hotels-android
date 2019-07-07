package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.Command;
import com.muflone.android.django_hotels.database.models.CommandUsage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment {
    private final Singleton singleton = Singleton.getInstance();
    private Context context;

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        List<SystemInformationValue> systemInformationValuesList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        // Execute START ABOUT BEGIN commands
        this.singleton.commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_ABOUT_BEGIN);

        AboutPage aboutPage = new AboutPage(this.context)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher_round)
                .setDescription(String.format(Locale.ROOT,
                        "%s %s\n%s",
                        this.singleton.settings.getApplicationName(),
                        this.singleton.settings.getApplicationVersion(),
                        this.getString(R.string.app_description)))
                .addPlayStore(this.getString(R.string.about_app_playstore))
                .addGitHub(this.getString(R.string.about_app_github));
        // Contacts section
        aboutPage.addGroup(this.getString(R.string.about_contacts))
                .addEmail(this.getString(R.string.author_email))
                .addWebsite(this.getString(R.string.author_web))
                .addTwitter(this.getString(R.string.author_twitter));
        // License section
        aboutPage.addGroup(this.getString(R.string.about_license))
                .addItem(this.getCopyRightsElement())
                .addWebsite(this.getString(R.string.about_license_url),
                        this.getString(R.string.about_app_license));
        // Credits section
        aboutPage.addGroup(this.getString(R.string.about_credits))
                .addGitHub(this.getString(R.string.about_zxing_url),
                        this.getString(R.string.about_zxing_title))
                .addGitHub(this.getString(R.string.about_about_url),
                        this.getString(R.string.about_about_title))
                .addGitHub(this.getString(R.string.about_guava_url),
                        this.getString(R.string.about_guava_title))
                .addGitHub(this.getString(R.string.about_numberprogressbar_url),
                        this.getString(R.string.about_numberprogressbar_title));
        // Application Information section
        aboutPage.addGroup(this.getString(R.string.about_application_information));
        // Application name
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_application_information_name,
                this.singleton.settings.getApplicationName()));
        // Application version
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_application_information_version,
                this.singleton.settings.getApplicationVersion()));
        // Package name
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_application_information_package,
                this.singleton.settings.getPackageName()));
        // Add Application Information elements
        for (SystemInformationValue value : systemInformationValuesList) {
            aboutPage.addItem(this.newSystemInformationElement(value.toString(false)));
            stringBuilder.append(value.toString(true));
        }
        systemInformationValuesList.clear();
        // Settings Information section
        aboutPage.addGroup(this.getString(R.string.about_settings_information));
        // Tablet ID
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.settings_tablet_id_title,
                this.singleton.settings.getTabletID()));
        // Tablet Key
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.settings_tablet_key_title,
                String.format("%s...%s",
                        Utility.left(this.singleton.settings.getTabletKey(), 4),
                        Utility.right(this.singleton.settings.getTabletKey(), 4))));
        // API URL
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.settings_api_url_title,
                this.singleton.settings.getApiUri().toString()));
        // Add Settings Information elements
        for (SystemInformationValue value : systemInformationValuesList) {
            aboutPage.addItem(this.newSystemInformationElement(value.toString(false)));
            stringBuilder.append(value.toString(true));
        }
        systemInformationValuesList.clear();
        // System Information section
        aboutPage.addGroup(this.getString(R.string.about_system_information));
        // Android version
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_android,
                Build.VERSION.RELEASE,
                Build.ID));
        // SDK API Level
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_sdk,
                String.valueOf(Build.VERSION.SDK_INT)));
        // ROM version
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_rom,
                Build.VERSION.INCREMENTAL));
        // Kernel version
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_kernel,
                System.getProperty("os.version")));
        // Architecture
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_architecture,
                System.getProperty("os.arch")));
        // Device Manufacturer
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_manufacturer,
                Build.MANUFACTURER));
        // Device Model
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_model,
                Build.MODEL));
        // Device Product
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_product,
                Build.PRODUCT));
        // Device Brand
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_brand,
                Build.BRAND));
        // Device
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_device,
                Build.DEVICE));
        // Display Resolution
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_resolution,
                String.valueOf(metrics.widthPixels),
                String.valueOf(metrics.heightPixels),
                String.valueOf(metrics.density)));
        // Timezone
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_timezone,
                TimeZone.getDefault().getID(),
                TimeZone.getDefault().getDisplayName(Locale.ROOT)));
        // Android ID
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_android_id,
                Settings.Secure.getString(
                        Objects.requireNonNull(this.getActivity()).getBaseContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID)));
        // User Agent
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_user_agent,
                System.getProperty("http.agent")));
        // Add System Information elements
        for (SystemInformationValue value : systemInformationValuesList) {
            aboutPage.addItem(this.newSystemInformationElement(value.toString(false)));
            stringBuilder.append(value.toString(true));
        }
        systemInformationValuesList.clear();
        // Add configured Commands elements
        for (String context : CommandConstants.contexts) {
            ArrayList<Command> commands = this.singleton.apiData.getCommandsByContext(context);
            aboutPage.addGroup(String.format(this.getString(R.string.about_configured_commands), context));
            stringBuilder.append(String.format(this.getString(R.string.about_configured_commands), context));
            stringBuilder.append("\n");
            if (commands.size() > 0) {
                // Add each configured command
                for (Command command : commands) {
                    CommandValue commandValue = new CommandValue(
                            this.getString(R.string.about_configured_commands_format), command);
                    aboutPage.addItem(this.newCommandElement(commandValue.toString(false),
                                                             commandValue.command.command.toString()));
                    stringBuilder.append(commandValue.toString(true));
                }
            } else {
                // No configured commands
                aboutPage.addItem(this.newDetailElement(this.getString(R.string.about_configured_commands_none)));
            }
        }
        // Feedback section
        aboutPage.addGroup(this.getString(R.string.about_feedback));
        // Copy details item
        aboutPage.addItem(getCopyDetailsElement(stringBuilder.toString()));
        // E-mail details item
        aboutPage.addItem(getEmailDetailsElement(stringBuilder.toString()));
        // Tools section
        aboutPage.addGroup(this.getString(R.string.about_tools));
        aboutPage.addItem(this.databaseBackupElement());
        // Execute START ABOUT END commands
        this.singleton.commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_ABOUT_END);
        return aboutPage.create();
    }

    private Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(this.getString(R.string.about_app_copyright),
                this.getString(R.string.author_name),
                Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.ic_copyright);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.START);
        copyRightsElement.setOnClickListener(view -> Toast.makeText(context, copyrights, Toast.LENGTH_SHORT).show());
        return copyRightsElement;
    }

    private Element getCopyDetailsElement(String value) {
        Element copyDetails = new Element();
        copyDetails.setTitle(this.getString(R.string.about_feedback_copy_details));
        copyDetails.setIconDrawable(R.drawable.ic_copy);
        copyDetails.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyDetails.setIconNightTint(android.R.color.white);
        copyDetails.setGravity(Gravity.START);
        copyDetails.setOnClickListener(view -> {
            Utility.copyToClipboard(context, value);
            Toast.makeText(context, R.string.about_feedback_copied_to_clipboard, Toast.LENGTH_SHORT).show();
        });
        return copyDetails;
    }

    private Element getEmailDetailsElement(String value) {
        Element copyDetails = new Element();
        copyDetails.setTitle(this.getString(R.string.about_feedback_email_details));
        copyDetails.setIconDrawable(R.drawable.ic_mail);
        copyDetails.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyDetails.setIconNightTint(android.R.color.white);
        copyDetails.setGravity(Gravity.START);
        copyDetails.setOnClickListener(view -> Utility.sendEmail(context,
                new String[]{this.getString(R.string.author_email)},
                String.format(Locale.ROOT,
                        this.getString(R.string.about_feedback_subject),
                        this.singleton.settings.getApplicationName(),
                        this.singleton.settings.getApplicationVersion()),
                value));
        return copyDetails;
    }

    private Element newDetailElement(String value) {
        // Return a generic element with information
        return new Element()
                .setIconDrawable(R.drawable.ic_info)
                .setTitle(value);
    }

    private Element newCommandElement(String text, String value) {
        // Return an element for Custom command
        return this.newDetailElement(text)
                .setOnClickListener(view -> Toast.makeText(context, value, Toast.LENGTH_LONG).show());
    }

    private Element newSystemInformationElement(String value) {
        // Return an element for System Information
        return this.newDetailElement(value)
                .setOnClickListener(view -> {
                    Utility.copyToClipboard(context, value);
                    Toast.makeText(context, R.string.about_feedback_copied_to_clipboard, Toast.LENGTH_SHORT).show();
                });
    }

    private Element databaseBackupElement() {
        Element backupElement = new Element();
        backupElement.setTitle(this.getString(R.string.about_tools_database_backup));
        backupElement.setIconDrawable(R.drawable.ic_settings);
        backupElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        backupElement.setIconNightTint(android.R.color.white);
        backupElement.setGravity(Gravity.START);
        backupElement.setOnClickListener(view -> {
            singleton.database.backupDatabase(context);
            Toast.makeText(context, R.string.about_tools_database_backup_done, Toast.LENGTH_LONG).show();
        });
        return backupElement;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    private class SystemInformationValue {
        // Container for System Information id and multiple values
        private final int id;
        private final List<String> values;

        @SuppressWarnings("WeakerAccess")
        public SystemInformationValue(int id, String... values) {
            // Add multiple values at once
            this.id = id;
            this.values = Arrays.asList(values);
        }

        @SuppressWarnings("WeakerAccess")
        public String toString(boolean newLine) {
            String result;
            if (getString(this.id).contains("%s")) {
                // Title already contains format specifiers
                result = String.format(Locale.ROOT, getString(this.id), this.values.toArray());
            } else {
                // With no format specifiers we add title: value standard format
                result = String.format(Locale.ROOT, "%s: %s", getString(this.id), this.values.get(0));
            }
            // Add a newline character if required
            if (newLine) {
                result += "\n";
            }
            return result;
        }
    }

    private class CommandValue {
        // Container for CommandValue
        private final String format;
        private final Command command;
        private final Singleton singleton = Singleton.getInstance();

        @SuppressWarnings("WeakerAccess")
        public CommandValue(String format, Command command) {
            this.format = format;
            this.command = command;
        }

        @SuppressWarnings("WeakerAccess")
        public String toString(boolean newLine) {
            CommandUsage commandUsage = this.singleton.apiData.commandsUsageMap.get(command.id);
            String result = String.format(this.format,
                                          command.id,
                                          command.name,
                                          command.type,
                                          commandUsage != null ? commandUsage.used : 0,
                                          command.uses);
            // Add a newline character if required
            if (newLine) {
                result += "\n";
            }
            return result;
        }
    }
}
