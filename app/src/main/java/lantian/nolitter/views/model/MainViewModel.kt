package lantian.nolitter.views.model

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lantian.nolitter.MainActivity
import lantian.nolitter.R
import lantian.nolitter.modules.DataStoreManager
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val dataStore: DataStoreManager) : ViewModel() {

    var topAppBarTitle: MutableState<String> = mutableStateOf("NoLitter")
    var topAppBarActions: MutableState<@Composable RowScope.() -> Unit> = mutableStateOf({})
    var appTheme: MutableState<String> = mutableStateOf(runBlocking { dataStore.getPreference("theme", "default") })

    fun <T> getPreference(key: String, defaultValue: T): T {
        var preferenceValue by mutableStateOf(defaultValue)
        viewModelScope.launch { preferenceValue = dataStore.getPreference(key, defaultValue) }
        return preferenceValue
    }

    fun <T> setPreference(key: String, value: T) {
        viewModelScope.launch { dataStore.setPreference(key, value) }
    }

    fun intentToWebsite(context: Context, link: String) {
        startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(link)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), null)
    }
    fun hideAppIcon(context: Context, value: Boolean) {
        val componentState = if (value) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        context.packageManager.setComponentEnabledSetting(ComponentName(context, MainActivity::class.java), componentState, PackageManager.DONT_KILL_APP)
    }
    fun getNavigationTitle(context: Context, key: String?): String {
        return when (key) {
            "PreferenceGeneral" -> context.resources.getString(R.string.ui_settings_general)
            "PreferenceInterface" -> context.resources.getString(R.string.ui_settings_interface)
            "PreferenceMiscellaneous" -> context.resources.getString(R.string.ui_settings_miscellaneous)
            "PreferenceSelectApps" -> context.resources.getString(R.string.ui_settings_forceMode)
            else -> context.resources.getString(R.string.app_name)
        }
    }
}