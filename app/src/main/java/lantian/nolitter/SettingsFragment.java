package lantian.nolitter;

import android.content.Context;
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
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.settings);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getPreferenceScreen().removePreference(findPreference("hidden"));
        findPreference("banned_ui").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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
    }

    public boolean ltChooseApp() {
        // Get app info
        final List<ApplicationInfo> appInfo = getActivity().getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(appInfo, new ApplicationInfo.DisplayNameComparator(getActivity().getPackageManager()));

        // Get current settings
        ArrayList<String> banned = new ArrayList<>(Arrays.asList(prefs.getString("banned", getString(R.string.settings_banned)).split(",")));

        // Form arrays
        final String[] appTitles = new String[appInfo.size()];
        boolean[] appCared = new boolean[appInfo.size()];
        for (int i = 0; i < appInfo.size(); i++) {
            appTitles[i] = appInfo.get(i).loadLabel(getActivity().getPackageManager()).toString();
            appCared[i] = banned.contains(appInfo.get(i).packageName);
        }

        // Make the dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.ui_chooseApp);
        dialog.setMultiChoiceItems(appTitles, appCared, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                ArrayList<String> banned = new ArrayList<>(Arrays.asList(prefs.getString("banned", getString(R.string.settings_banned)).split(",")));
                if (isChecked) {
                    banned.add(appInfo.get(which).packageName);
                    Toast.makeText(getActivity(), appTitles[which] + " " + getString(R.string.toast_whitelist_add), Toast.LENGTH_SHORT).show();
                } else {
                    banned.remove(appInfo.get(which).packageName);
                    Toast.makeText(getActivity(), appTitles[which] + " " + getString(R.string.toast_whitelist_remove), Toast.LENGTH_SHORT).show();
                }
                String newBanned = "";
                for (String bannedFragment : banned) {
                    if (!bannedFragment.trim().isEmpty()) newBanned += bannedFragment.trim() + ",";
                }
                prefs.edit().putString("banned", newBanned).apply();
            }
        });
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return false;
    }
}
