package lantian.nolitter.views.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lantian.nolitter.database.PackagePreference
import lantian.nolitter.repository.InstalledPackageInfo
import lantian.nolitter.repository.PreferenceRepository
import lantian.nolitter.repository.ProcessPackageInfoPreference
import javax.inject.Inject

@HiltViewModel
class PackageViewModel @Inject constructor(private val preferenceRepository : PreferenceRepository) : ViewModel() {

    var packageInfo by mutableStateOf(listOf<InstalledPackageInfo>())
    var currentPackagePreference: PackagePreference? by mutableStateOf(null)
    var customizedPackages: List<String> = listOf()

    init {
        viewModelScope.launch {
            packageInfo = preferenceRepository.getInstalledPackageInfo()
            customizedPackages = preferenceRepository.getCustomizedPackages()
        }
    }

    fun getPackageInfo(packageName: String): InstalledPackageInfo {
        return packageInfo.first { it.packageName == packageName }
    }

    fun <T> getPreference(key: String, defaultValue: T): T {
        return runBlocking { preferenceRepository.getPreference(key, defaultValue) }
    }

    fun <T> setPreference(key: String, value: T) {
        return runBlocking { preferenceRepository.setPreference(key, value) }
    }

    fun getPackagePreference(packageName: String) {
        currentPackagePreference = null
        viewModelScope.launch { currentPackagePreference = preferenceRepository.getPackagePreference(packageName) }
    }

    fun setPackagePreference(preference: PackagePreference) {
        currentPackagePreference = preference
        viewModelScope.launch { preferenceRepository.setPackagePreference(preference) }
    }

    fun isCustomizedPackages(packageName: String): Boolean {
        return customizedPackages.contains(packageName)
    }

    fun onChangeCustomizedPackages(packageName: String, newValue: Boolean) {
        val customizedPackagesList = customizedPackages.toMutableList()
        if (newValue) customizedPackagesList.add(packageName) else customizedPackagesList.remove(packageName)
        customizedPackages = customizedPackagesList
        viewModelScope.launch { preferenceRepository.onChangeCustomizedPackages(customizedPackagesList) }
    }

    fun getDefaultProcessPreference(): ProcessPackageInfoPreference {
        return runBlocking { preferenceRepository.getDefaultProcessPreference() }
    }
}
