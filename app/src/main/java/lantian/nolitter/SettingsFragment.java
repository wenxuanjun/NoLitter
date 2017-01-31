package lantian.nolitter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SettingsFragment extends PreferenceFragment {
    public static SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        findPreference("banned").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ltChooseApp();
                return false;
            }
        });
        findPreference("author").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lantian.pub"));
                startActivity(browserIntent);
                return false;
            }
        });
        findPreference("source").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/xddxdd/lantian-nolitter"));
                startActivity(browserIntent);
                return false;
            }
        });
        Preference.OnPreferenceChangeListener updateXposed = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (ltReload()) {
                    Toast.makeText(getActivity(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_savedRestart), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        };
        findPreference("saveLog").setOnPreferenceChangeListener(updateXposed);
        findPreference("sdcard").setOnPreferenceChangeListener(updateXposed);
        findPreference("banned").setOnPreferenceChangeListener(updateXposed);
    }

    public boolean ltChooseApp() {
        // Get app info
        final List<ApplicationInfo> appInfo = getActivity().getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(appInfo, new ApplicationInfo.DisplayNameComparator(getActivity().getPackageManager()));

        // Get current settings
        final ArrayList<String> banned = new ArrayList<>(Arrays.asList(prefs.getString("banned", getString(R.string.settings_banned)).split(",")));

        // Form arrays
        String[] appTitles = new String[appInfo.size()];
        boolean[] appCared = new boolean[appInfo.size()];
        for (int i = 0; i < appInfo.size(); i++) {
            appTitles[i] = appInfo.get(i).loadLabel(getActivity().getPackageManager()).toString() + " [" + appInfo.get(i).packageName + "]";
            appCared[i] = banned.contains(appInfo.get(i).packageName);
        }

        // Make the dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.ui_chooseApp);
        dialog.setMultiChoiceItems(appTitles, appCared, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    banned.add(appInfo.get(which).packageName);
                } else {
                    banned.remove(appInfo.get(which).packageName);
                }
                String newBanned = "";
                for (String bannedItem : banned) {
                    if (newBanned.isEmpty()) {
                        newBanned += bannedItem;
                    } else {
                        newBanned += "," + bannedItem;
                    }
                }
                prefs.edit().putString("banned", newBanned).apply();
            }
        });
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (ltReload()) {
                    Toast.makeText(getActivity(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_savedRestart), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
        return false;
    }

    public boolean ltReload() {
        // Wait for Xposed to take control
        return false;
    }
}
