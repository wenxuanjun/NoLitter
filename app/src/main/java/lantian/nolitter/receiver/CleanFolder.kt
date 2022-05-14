package lantian.nolitter.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import lantian.nolitter.BuildConfig
import lantian.nolitter.Constants
import lantian.nolitter.R
import java.io.File
import java.io.IOException
import java.net.URI

class CleanFolder: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Decode package name
        var packageName = intent.dataString
        if (packageName!!.startsWith("package:")) packageName = packageName.substring(8)
        if (packageName.isEmpty()) return

        // Get the preference
        val prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID + "_preferences", Context.MODE_WORLD_READABLE)

        // Check if is enabled
        if (!prefs.getBoolean("remove_after_uninstall", true)) return

        // Check if have sdcard access permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, packageName + context.getString(R.string.ui_cleanFolder_failClear), Toast.LENGTH_SHORT).show()
            return
        }

        // Check if it is replace not uninstall
        val existingpackageNames = context.packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES)
        for (existingpackageName in existingpackageNames) {
            if (existingpackageName.packageName == packageName) return
        }

        // Delete files and folders if exist
        val directoryPath = "/sdcard" + prefs.getString("redirect_dir", Constants.defaultRedirectDir) + "/" + packageName
        if (File(URI.create("file://$directoryPath")).exists()) {
            try {
                Toast.makeText(context, packageName + context.getString(R.string.ui_cleanFolder_isClearing), Toast.LENGTH_SHORT).show()
                Runtime.getRuntime().exec("rm -r $directoryPath")
                Toast.makeText(context, packageName + context.getString(R.string.ui_cleanFolder_isCleared), Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(context, packageName + context.getString(R.string.ui_cleanFolder_failClear), Toast.LENGTH_SHORT).show()
            }
        }
    }
}