package lantian.nolitter;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class CleanFolderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /* Decode package name */
        String pkg = intent.getDataString();
        if (pkg.startsWith("package:")) pkg = pkg.substring(8);
        if (pkg.isEmpty()) return;

        /* Check if is enabled */
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("remove_after_uninstall", true))
            return;

        /* Check if have sdcard access permission */
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, pkg + context.getString(R.string.ui_failClear), Toast.LENGTH_SHORT).show();
            return;
        }

        /* Check if it is replace not uninstall */
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> existingPkgs = pm.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        for (PackageInfo existingPkg : existingPkgs) {
            if (existingPkg.packageName.equals(pkg)) return;
        }

        /* Delete files and folders */
        Toast.makeText(context, pkg + context.getString(R.string.ui_isUninstalled), Toast.LENGTH_SHORT).show();
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("rm -r /sdcard/Android/files/" + pkg);
            /* Deal with some apps that use sdcard relative position */
            runtime.exec("rm -r /sdcard/Android/files/Android/data/" + pkg);
            Toast.makeText(context, pkg + context.getString(R.string.ui_isCleared), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, pkg + context.getString(R.string.ui_failClear), Toast.LENGTH_SHORT).show();
        }
    }
}
