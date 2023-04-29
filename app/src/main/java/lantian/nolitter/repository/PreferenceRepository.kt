package lantian.nolitter.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.PackageManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lantian.nolitter.database.PackagePreference
import lantian.nolitter.database.PackagePreferenceDao
import lantian.nolitter.modules.DataStoreManager
import javax.inject.Inject
import javax.inject.Singleton

data class InstalledPackageInfo (
    val appName: String, val appIcon: Drawable,
    val isSystem: Boolean, val isModule: Boolean,
    val packageName: String, val firstInstallTime: Long,
)

data class ProcessPackageInfoPreference(
    val sortedBy: String, val hideSystem: Boolean,
    val hideModule: Boolean
)

@Singleton
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
        return databaseDataSource.queryPackage(packageName) ?: PackagePreference(packageName = packageName)
    }

    suspend fun setPackagePreference(packagePreference: PackagePreference) {
        val isPreferenceExist = databaseDataSource.queryPackage(packagePreference.packageName) != null
        if (isPreferenceExist) databaseDataSource.updatePackage(packagePreference)
        else databaseDataSource.insertPackage(packagePreference)
    }

    suspend fun getCustomizedPackages(): List<String> {
        val customizedPackages = dataStoreDataSource.getPreference("customized_packages", "")
        return customizedPackages.split(":")
    }

    suspend fun onChangeCustomizedPackages(customizedPackagesList: List<String>) {
        dataStoreDataSource.setPreference("customized_packages", customizedPackagesList.joinToString(":"))
    }

    suspend fun getDefaultProcessPreference(): ProcessPackageInfoPreference = ProcessPackageInfoPreference(
        sortedBy = dataStoreDataSource.getPreference("select_sortedBy", "app_name"),
        hideSystem = dataStoreDataSource.getPreference("select_hideSystem", true),
        hideModule = dataStoreDataSource.getPreference("select_hideModule", true)
    )

    @Suppress("DEPRECATION")
    suspend fun getInstalledPackageInfo(): List<InstalledPackageInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val allPackageInfo: ArrayList<InstalledPackageInfo> = ArrayList()
        for (installedPackage in packageManager.getInstalledPackages(PackageManager.GET_META_DATA)) {
            val applicationInfo = installedPackage.applicationInfo
            allPackageInfo.add(InstalledPackageInfo(
                appName = applicationInfo.loadLabel(packageManager).toString(),
                appIcon = applicationInfo.loadIcon(packageManager),
                isSystem = applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0,
                isModule = applicationInfo.metaData != null && applicationInfo.metaData.containsKey("xposedminversion"),
                packageName = applicationInfo.packageName,
                firstInstallTime = installedPackage.firstInstallTime
            ))
        }
        return@withContext allPackageInfo
    }
}