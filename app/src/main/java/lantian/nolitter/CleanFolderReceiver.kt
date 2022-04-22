package lantian.nolitter

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.preference.PreferenceManager
import java.io.IOException

class CleanFolderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Decode package name
        var packageName = intent.dataString
        if (packageName!!.startsWith("package:")) packageName = packageName.substring(8)
        if (packageName.isEmpty()) return

        // Check if is enabled
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("remove_after_uninstall", true)) return

        /* Check if have sdcard access permission */if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, packageName + context.getString(R.string.ui_failClear), Toast.LENGTH_SHORT).show()
            return
        }

        // Check if it is replace not uninstall
        val pm = context.packageManager
        val existingpackageNames = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES)
        for (existingpackageName in existingpackageNames) {
            if (existingpackageName.packageName == packageName) return
        }

        // Delete files and folders
        Toast.makeText(context, packageName + context.getString(R.string.ui_isUninstalled), Toast.LENGTH_SHORT).show()
        val runtime = Runtime.getRuntime()
        try {
            runtime.exec("rm -r /sdcard/Android/files/$packageName")
            // Deal with some apps that use sdcard relative position
            runtime.exec("rm -r /sdcard/Android/files/Android/data/$packageName")
            Toast.makeText(context, packageName + context.getString(R.string.ui_isCleared), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(context, packageName + context.getString(R.string.ui_failClear), Toast.LENGTH_SHORT).show()
        }
    }
}