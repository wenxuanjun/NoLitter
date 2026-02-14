package lantian.nolitter.views.model

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import lantian.nolitter.MainActivity
import lantian.nolitter.R
import lantian.nolitter.modules.DataStoreManager
import javax.inject.Inject

data class AppBarContent(
    var title: @Composable () -> Unit = {},
    var actions: @Composable RowScope.() -> Unit = {},
)

private val appBarTitles = mapOf(
    "home" to R.string.app_name,
    "general" to R.string.ui_settings_general,
    "interface" to R.string.ui_settings_interface,
    "miscellaneous" to R.string.ui_settings_miscellaneous,
    "packages" to R.string.ui_settings_packages
)

@HiltViewModel
class MainViewModel @Inject constructor(private val dataStore: DataStoreManager) : ViewModel() {
    var appBarContent by mutableStateOf(AppBarContent())
    var currentAppTheme by mutableStateOf(dataStore.getPreferenceSync("theme", "default"))

    companion object {
        @JvmStatic
        fun intentToWebsite(context: Context, link: String) {
            val intent = Intent(Intent.ACTION_VIEW, link.toUri())
            context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), null)
        }

        @JvmStatic
        fun getNavigationTitle(context: Context, key: String?): String {
            return context.getString(appBarTitles.getOrDefault(key, R.string.app_name))
        }

        @JvmStatic
        fun hideAppIcon(context: Context, value: Boolean) {
            context.packageManager.setComponentEnabledSetting(
                ComponentName(context, MainActivity::class.java),
                if (value) COMPONENT_ENABLED_STATE_DISABLED else COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }

    fun <T> getPreference(key: String, defaultValue: T): T {
        var preferenceValue by mutableStateOf(defaultValue)
        viewModelScope.launch {
            preferenceValue = dataStore.getPreference(key, defaultValue)
        }
        return preferenceValue
    }

    fun <T> setPreference(key: String, value: T) {
        viewModelScope.launch { dataStore.setPreference(key, value) }
    }
}
