package lantian.nolitter;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

        EditText editArea2 = (EditText) findViewById(R.id.editText2);
        String banned = prefs.getString("banned", Common.bannedDefault);
        editArea2.setText(banned.replace(",", "\n"));

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setChecked(prefs.getBoolean("saveLog", true));

    }

    public void ltSave(View v) {
        EditText editArea = (EditText) findViewById(R.id.editText);
        EditText editArea2 = (EditText) findViewById(R.id.editText2);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        String sdcard = editArea.getText().toString();
        String banned = editArea2.getText().toString();
        if(sdcard.contains(",") || banned.contains(",")) {
            Toast.makeText(this, getString(R.string.toast_invalidchar), Toast.LENGTH_SHORT).show();
            return;
        }
        prefs.edit()
            .putString("sdcard", sdcard.replace("\n", ","))
            .putString("banned", banned.replace("\n", ","))
            .putBoolean("saveLog", checkBox.isChecked())
            .apply();
        if(ltReload()) {
            Toast.makeText(this, getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.toast_saved_restart), Toast.LENGTH_SHORT).show();
        }
    }

    public void ltReset(View v) {
        EditText editArea = (EditText) findViewById(R.id.editText);
        editArea.setText(Common.sdcardDefault.replaceAll(",", "\n"));
        EditText editArea2 = (EditText) findViewById(R.id.editText2);
        editArea2.setText(Common.bannedDefault.replaceAll(",", "\n"));
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
            Toast.makeText(this, getString(R.string.toast_reset_restart), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean ltReload() {
        // Wait for Xposed to take control
        return false;
    }
}
