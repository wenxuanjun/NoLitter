package lantian.nolitter.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lantian.nolitter.database.PackagePreference
import lantian.nolitter.database.PackagePreferenceDao
import lantian.nolitter.modules.DataStoreManager
import javax.inject.Inject

data class InstalledPackageInfo (
    val appName: String, val appIcon: Drawable,
    val isSystem: Boolean, val isModule: Boolean,
    val packageName: String, val firstInstallTime: Long,
)

data class ProcessPackageInfoPreference(
    val sortedBy: String, val hideSystem: Boolean,
    val hideModule: Boolean
)

class PreferenceRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStoreDataSource: DataStoreManager,
    private val databaseDataSource: PackagePreferenceDao
) {

    suspend fun <T> getPreference(key: String, defaultValue: T): T {
        return dataStoreDataSource.getPreference(key, defaultValue)
    }

    suspend fun <T> setPreference(key: String, value: T) {
        return dataStoreDataSource.setPreference(key, value)
    }

    suspend fun getPackagePreference(packageName: String): PackagePreference {
        return if (isCustomizedPackages(packageName)) { databaseDataSource.queryPackage(packageName) }
        else throw RuntimeException("$packageName is not customized so it will not be in database")
    }

    suspend fun setPackagePreference(packagePreference: PackagePreference) {
        databaseDataSource.updatePackage(packagePreference)
    }

    suspend fun isCustomizedPackages(packageName: String): Boolean {
        val customizedPackages = dataStoreDataSource.getPreference("customized_packages", "")
        return customizedPackages.split(":").contains(packageName)
    }

    suspend fun onChangeCustomizedPackages(packageName: String, newValue: Boolean) {
        val customizedPackages = dataStoreDataSource.getPreference("customized_packages", "")
        val customizedPackagesList = customizedPackages.split(":").toMutableList()
        if (newValue) customizedPackagesList.add(packageName) else customizedPackagesList.remove(packageName)
        dataStoreDataSource.setPreference("customized_packages", customizedPackagesList.joinToString(":"))
    }

    suspend fun getDefaultProcessPreference(): ProcessPackageInfoPreference = ProcessPackageInfoPreference(
        sortedBy = dataStoreDataSource.getPreference("select_sortedBy", "app_name"),
        hideSystem = dataStoreDataSource.getPreference("select_hideSystem", true),
        hideModule = dataStoreDataSource.getPreference("select_hideModule", true)
    )

    suspend fun getInstalledPackageInfo(): List<InstalledPackageInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val allPackageInfo: ArrayList<InstalledPackageInfo> = ArrayList()
        for (installedPackage in packageManager.getInstalledPackages(PackageManager.GET_META_DATA)) {
            val applicationInfo = installedPackage.applicationInfo
            val applicationFlag = installedPackage.applicationInfo.flags
            allPackageInfo.add(InstalledPackageInfo(
                appName = applicationInfo.loadLabel(packageManager).toString(),
                appIcon = applicationInfo.loadIcon(packageManager),
                isSystem = applicationFlag and ApplicationInfo.FLAG_SYSTEM != 0,
                isModule = applicationInfo.metaData != null && applicationInfo.metaData.containsKey("xposedminversion"),
                packageName = applicationInfo.packageName,
                firstInstallTime = installedPackage.firstInstallTime
            ))
        }
        return@withContext allPackageInfo
    }
}