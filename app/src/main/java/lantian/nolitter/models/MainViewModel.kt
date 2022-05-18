package lantian.nolitter.models

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lantian.nolitter.BuildConfig
import lantian.nolitter.Constants
import lantian.nolitter.MainActivity
import lantian.nolitter.R

data class InstalledPackageInfo (
    val appName: String, val appIcon: Drawable,
    val isSystem: Boolean, val isModule: Boolean,
    val isForced: Boolean, val packageName: String,
    val firstInstallTime: Long
)

data class ProcessPackageInfoPreference(
    val sortedBy: String, val hideSystem: Boolean,
    val hideModule: Boolean
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var activity: Application = application
    private val preferencesName = BuildConfig.APPLICATION_ID + "_preferences"
    private var sharedPreferences: SharedPreferences = activity.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

    // Several states of the view
    var topAppBarTitle: MutableState<String> = mutableStateOf(activity.resources.getString(R.string.app_name))
    var topAppBarActions: MutableState<@Composable RowScope.() -> Unit> = mutableStateOf({})
    val installedPackages: MutableState<List<InstalledPackageInfo>> = mutableStateOf(listOf())
    var appTheme: MutableState<String> = mutableStateOf(getStringPreference("theme", "default"))

    // Intent to some webpages
    fun intentToWebsite(link: String) = startActivity(activity, Intent(Intent.ACTION_VIEW, Uri.parse(link)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), null)

    // Functions of handling preferences
    fun getBooleanPreference(key: String, defaultValue: Boolean): Boolean { return sharedPreferences.getBoolean(key, defaultValue) }
    fun getStringPreference(key: String, defaultValue: String): String { return sharedPreferences.getString(key, defaultValue) ?: defaultValue }
    fun setBooleanPreference(key: String, value: Boolean) { sharedPreferences.edit().putBoolean(key, value).apply() }
    fun setStringPreference(key: String, value: String) { sharedPreferences.edit().putString(key, value).apply() }

    // Other useful utils
    fun hideAppIcon(value: Boolean) {
        val componentState = if (value) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        activity.packageManager.setComponentEnabledSetting(ComponentName(activity, MainActivity::class.java), componentState, PackageManager.DONT_KILL_APP)
    }
    fun initAllInstalledPackages() {
        viewModelScope.launch { installedPackages.value = getAllInstalledPackages() }
    }
    fun getNavigationTitle(key: String?): String {
        return when (key) {
            "PreferenceGeneral" -> activity.resources.getString(R.string.ui_settings_general)
            "PreferenceInterface" -> activity.resources.getString(R.string.ui_settings_interface)
            "PreferenceMiscellaneous" -> activity.resources.getString(R.string.ui_settings_miscellaneous)
            "PreferenceSelectApps" -> activity.resources.getString(R.string.ui_settings_forceMode)
            else -> activity.resources.getString(R.string.app_name)
        }
    }
    fun getDefaultProcessPreference(): ProcessPackageInfoPreference = ProcessPackageInfoPreference(
        sortedBy = getStringPreference("select_sortedBy", "app_name"),
        hideSystem = getBooleanPreference("select_hideSystem", true),
        hideModule = getBooleanPreference("select_hideModule", true)
    )
    fun onChangeForcedApps(packageName: String, newValue: Boolean) {
        val forcedApps = getStringPreference("forced_apps", Constants.defaultForcedList).split(":").toMutableList()
        if (newValue) forcedApps.add(packageName) else forcedApps.remove(packageName)
        setStringPreference("forced_apps", forcedApps.joinToString(":"))
    }

    // Private functions
    private suspend fun getAllInstalledPackages(): ArrayList<InstalledPackageInfo> = withContext(Dispatchers.IO) {
        val packageManager = activity.packageManager
        val allPackageInfo: ArrayList<InstalledPackageInfo> = ArrayList()
        val forcedApps = getStringPreference("forced_apps", Constants.defaultForcedList).split(":")
        val installedPackages: List<PackageInfo> = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        for (installedPackage in installedPackages) {
            val applicationInfo = installedPackage.applicationInfo
            val applicationFlag = installedPackage.applicationInfo.flags
            allPackageInfo.add(InstalledPackageInfo(
                appName = applicationInfo.loadLabel(packageManager).toString(),
                appIcon = applicationInfo.loadIcon(packageManager),
                isSystem = (applicationFlag and ApplicationInfo.FLAG_SYSTEM != 0) || (applicationFlag and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0),
                isModule = applicationInfo.metaData != null && applicationInfo.metaData.containsKey("xposedminversion"),
                isForced = forcedApps.contains(applicationInfo.packageName),
                packageName = applicationInfo.packageName,
                firstInstallTime = installedPackage.firstInstallTime
            ))
        }
        return@withContext allPackageInfo
    }
}