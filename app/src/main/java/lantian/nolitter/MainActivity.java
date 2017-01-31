package lantian.nolitter;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getPreferences(MODE_WORLD_READABLE);

        EditText editArea = (EditText) findViewById(R.id.editText);
        String sdcard = prefs.getString("sdcard", Common.sdcardDefault);
        editArea.setText(sdcard.replace(",", "\n"));

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setChecked(prefs.getBoolean("saveLog", true));

    }

    public void ltSave(View v) {
        EditText editArea = (EditText) findViewById(R.id.editText);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        String sdcard = editArea.getText().toString();
        if(sdcard.contains(",")) {
            Toast.makeText(this, getString(R.string.toast_invalidChar), Toast.LENGTH_SHORT).show();
            return;
        }
        prefs.edit()
            .putString("sdcard", sdcard.replace("\n", ","))
            .putBoolean("saveLog", checkBox.isChecked())
            .apply();
        if(ltReload()) {
            Toast.makeText(this, getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.toast_savedRestart), Toast.LENGTH_SHORT).show();
        }
    }

    public void ltReset(View v) {
        EditText editArea = (EditText) findViewById(R.id.editText);
        editArea.setText(Common.sdcardDefault.replaceAll(",", "\n"));
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setChecked(prefs.getBoolean("saveLog", true));
        prefs.edit()
            .putString("sdcard", Common.sdcardDefault)
            .putString("banned", Common.bannedDefault)
            .putBoolean("saveLog", true)
            .apply();
        if(ltReload()) {
            Toast.makeText(this, getString(R.string.toast_reset), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.toast_resetRestart), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean ltReload() {
        // Wait for Xposed to take control
        return false;
    }

    public void ltChooseApp(View v) {
        // Get app info
        final List<ApplicationInfo> appInfo = this.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(appInfo, new ApplicationInfo.DisplayNameComparator(this.getPackageManager()));

        // Get current settings
        final ArrayList<String> banned = new ArrayList<>(Arrays.asList(prefs.getString("banned", Common.bannedDefault).split(",")));

        // Form arrays
        String[] appTitles = new String[appInfo.size()];
        boolean[] appCared = new boolean[appInfo.size()];
        for(int i = 0; i < appInfo.size(); i++) {
            appTitles[i] = appInfo.get(i).loadLabel(this.getPackageManager()).toString() + " [" + appInfo.get(i).packageName + "]";
            appCared[i] = banned.contains(appInfo.get(i).packageName);
        }

        // Make the dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.ui_chooseApp);
        dialog.setMultiChoiceItems(appTitles, appCared, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked) {
                    banned.add(appInfo.get(which).packageName);
                } else {
                    banned.remove(appInfo.get(which).packageName);
                }
                String newBanned = "";
                for(String bannedItem: banned) {
                    if(newBanned.isEmpty()) {
                        newBanned += bannedItem;
                    } else {
                        newBanned += "," + bannedItem;
                    }
                }
                prefs.edit().putString("banned", newBanned).apply();
            }
        });
        dialog.setPositiveButton(R.string.ui_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(ltReload()) {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_savedRestart), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
}
