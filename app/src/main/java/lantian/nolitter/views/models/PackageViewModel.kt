package lantian.nolitter.views.models

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
    init {
        viewModelScope.launch { packageInfo = preferenceRepository.getInstalledPackageInfo() }
    }

    fun getPackageInfo(packageName: String): InstalledPackageInfo {
        return packageInfo.first { it.packageName == packageName }
    }

    fun getPackagePreference(packageName: String) {
        currentPackagePreference = null
        viewModelScope.launch { currentPackagePreference = preferenceRepository.getPackagePreference(packageName) }
    }

    fun setPackagePreference(preference: PackagePreference) {
        currentPackagePreference = preference
        viewModelScope.launch { preferenceRepository.setPackagePreference(preference) }
    }

    fun getDefaultProcessPreference(): ProcessPackageInfoPreference {
        return runBlocking { preferenceRepository.getDefaultProcessPreference() }
    }
}