package lantian.nolitter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsFragment mySettings = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, mySettings).commit();
    }
}
