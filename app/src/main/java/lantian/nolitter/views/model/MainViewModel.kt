package lantian.nolitter.views.model

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lantian.nolitter.MainActivity
import lantian.nolitter.R
import lantian.nolitter.modules.DataStoreManager
import javax.inject.Inject

data class TopAppBarContent(
    var title: @Composable () -> Unit = {},
    var actions: @Composable RowScope.() -> Unit = {},
)

@HiltViewModel
class MainViewModel @Inject constructor(private val dataStore: DataStoreManager) : ViewModel() {

    var topAppBarContent by mutableStateOf(TopAppBarContent())
    var appTheme by mutableStateOf(runBlocking { dataStore.getPreference("theme", "default") })

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
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, MainActivity::class.java),
            if (value) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun getNavigationTitle(context: Context, key: String?): String = when (key) {
        "general" -> context.resources.getString(R.string.ui_settings_general)
        "interface" -> context.resources.getString(R.string.ui_settings_interface)
        "miscellaneous" -> context.resources.getString(R.string.ui_settings_miscellaneous)
        "packages" -> context.resources.getString(R.string.ui_settings_packages)
        else -> context.resources.getString(R.string.app_name)
    }
}