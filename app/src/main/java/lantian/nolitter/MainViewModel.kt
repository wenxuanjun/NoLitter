package lantian.nolitter

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

data class InstalledPackageInfo (val appName: String, val appIcon: Drawable, val isSystem: Boolean, val isModule: Boolean, val isForced: Boolean, val packageName: String)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var activity: Application = application
    private var sharedPreferences: SharedPreferences? = try {
        activity.getSharedPreferences(BuildConfig.APPLICATION_ID + "_preferences", Context.MODE_WORLD_READABLE)
    } catch (e: SecurityException) { null }

    var appTheme: MutableState<String?> = mutableStateOf(getStringPreference("theme", "default"))
    var topAppBarTitle: MutableState<String> = mutableStateOf(activity.resources.getString(R.string.app_name))
    fun isAvailable(): Boolean { return sharedPreferences != null }
    fun getBooleanPreference(key: String, defaultValue: Boolean): Boolean { return sharedPreferences?.getBoolean(key, defaultValue) ?: defaultValue }
    fun getStringPreference(key: String, defaultValue: String): String { return sharedPreferences?.getString(key, defaultValue) ?: defaultValue }
    fun setBooleanPreference(key: String, value: Boolean) { sharedPreferences!!.edit().putBoolean(key, value).apply() }
    fun setStringPreference(key: String, value: String) { sharedPreferences!!.edit().putString(key, value).apply() }
    fun hideAppIcon(value: Boolean) {
        activity.packageManager.setComponentEnabledSetting(
            ComponentName(activity, MainActivity::class.java),
            if (value) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
    fun getNavigationTitle(key: String?): String {
        return when (key) {
            "PreferenceGeneral" -> activity.resources.getString(R.string.ui_settings_general)
            "PreferenceInterface" -> activity.resources.getString(R.string.ui_settings_interface)
            "PreferenceAdvanced" -> activity.resources.getString(R.string.ui_settings_advanced)
            "PreferenceSelectApps" -> activity.resources.getString(R.string.ui_settings_forceMode)
            else -> activity.resources.getString(R.string.app_name)
        }
    }
    fun getAllInstalledPackages(): ArrayList<InstalledPackageInfo> {
        val packageManager = activity.packageManager
        val installedPackages: List<PackageInfo> = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        var allPackageInfo: ArrayList<InstalledPackageInfo> = ArrayList()
        val forcedApps = getStringPreference("forced", "").split(",")
        for (installedPackage in installedPackages) {
            val applicationInfo = installedPackage.applicationInfo
            val applicationFlag = installedPackage.applicationInfo.flags
            allPackageInfo.add(InstalledPackageInfo(
                appName = applicationInfo.loadLabel(packageManager).toString(),
                appIcon = applicationInfo.loadIcon(packageManager),
                isSystem = (applicationFlag and ApplicationInfo.FLAG_SYSTEM != 0) || (applicationFlag and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0),
                isModule = applicationInfo.metaData != null && applicationInfo.metaData.containsKey("xposedmodule"),
                isForced = forcedApps.contains(applicationInfo.packageName),
                packageName = applicationInfo.packageName,
            ))
        }
        return allPackageInfo
    }
    fun onChangeForcedApps(packageName: String, newValue: Boolean) {
        val forcedApps = getStringPreference("forced", "").split(",").toMutableList()
        if (newValue) forcedApps.add(packageName) else forcedApps.remove(packageName)
        var newForcedApps = ""
        for (forcedApp in forcedApps) {
            if (forcedApp.trim().isNotEmpty()) newForcedApps += forcedApp.trim() + ","
        }
        setStringPreference("forced", newForcedApps)
    }
}