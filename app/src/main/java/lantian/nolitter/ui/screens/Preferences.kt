package lantian.nolitter.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import lantian.nolitter.Constants
import lantian.nolitter.R
import lantian.nolitter.models.MainViewModel
import lantian.nolitter.ui.composables.*

@Composable
fun PreferenceHome( viewModel: MainViewModel, navController: NavHostController) {
    Column {
        ClickablePreferenceItem(
            text = viewModel.getNavigationTitle("PreferenceGeneral"),
            secondaryText = stringResource(R.string.ui_settings_general_description),
            icon = { Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.padding(8.dp)) },
            onClick = { navController.navigate("PreferenceGeneral") }
        )
        ClickablePreferenceItem(
            text = viewModel.getNavigationTitle("PreferenceInterface"),
            secondaryText = stringResource(R.string.ui_settings_interface_description),
            icon = { Icon(imageVector = Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.padding(8.dp)) },
            onClick = { navController.navigate("PreferenceInterface") }
        )
        ClickablePreferenceItem(
            text = viewModel.getNavigationTitle("PreferenceAdvanced"),
            secondaryText = stringResource(R.string.ui_settings_advanced_description),
            icon = { Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = null, modifier = Modifier.padding(8.dp)) },
            onClick = { navController.navigate("PreferenceAdvanced") }
        )
    }
}

@Composable
fun PreferenceGeneral(viewModel: MainViewModel, navController: NavHostController) {
    Column {
        ClickablePreferenceItem(
            text = stringResource(R.string.ui_settings_forceMode),
            secondaryText = stringResource(R.string.ui_settings_forceMode_description),
            onClick = { navController.navigate("PreferenceSelectApps") }
        )
        CheckBoxPreference(
            text = stringResource(R.string.ui_settings_separateApp),
            secondaryText = stringResource(R.string.ui_settings_separateApp_description),
            onChange = { viewModel.setBooleanPreference("separate_app", it) },
            defaultValue = viewModel.getBooleanPreference("separate_app", true)
        )
        CheckBoxPreference(
            text = stringResource(R.string.ui_settings_removeAfterUninstall),
            secondaryText = stringResource(R.string.ui_settings_removeAfterUninstall_description),
            onChange = { viewModel.setBooleanPreference("remove_after_uninstall", it) },
            defaultValue = viewModel.getBooleanPreference("remove_after_uninstall", true)
        )
    }
}

@Composable
fun PreferenceInterface(viewModel: MainViewModel) {
    Column {
        val selectedThemeKey = viewModel.getStringPreference("theme", "default")
        val themeOptions = mapOf(
            Pair("default", stringResource(R.string.ui_settings_theme_default)),
            Pair("light", stringResource(R.string.ui_settings_theme_light)),
            Pair("dark", stringResource(R.string.ui_settings_theme_dark))
        )
        var selectedTheme by remember { mutableStateOf(themeOptions[selectedThemeKey]) }
        ListPreference(
            text = stringResource(R.string.ui_settings_theme),
            secondaryText = selectedTheme,
            dialogTitle = stringResource(R.string.ui_settings_theme),
            defaultValue = selectedThemeKey,
            options = themeOptions
        ) {
            viewModel.setStringPreference("theme", it)
            selectedTheme = themeOptions[it]
            viewModel.appTheme.value = it
        }
        CheckBoxPreference(
            text = stringResource(R.string.ui_settings_hideIcon),
            secondaryText = stringResource(R.string.ui_settings_hideIcon_description),
            defaultValue = viewModel.getBooleanPreference("hide_icon", false),
            onChange = {
                viewModel.setBooleanPreference("hide_icon", it)
                viewModel.hideAppIcon(it)
            }
        )
    }
}

@Composable
fun PreferenceAdvanced(viewModel: MainViewModel) {
    Column {
        EditTextPreference(
            text = stringResource(R.string.ui_settings_redirectDir),
            secondaryText = stringResource(R.string.ui_settings_redirectDir_description),
            dialogTitle = stringResource(R.string.ui_settings_redirectDir),
            dialogDefaultContent = viewModel.getStringPreference("redirect_dir", Constants.defaultRedirectDir),
            onSubmit = { viewModel.setStringPreference("redirect_dir", it) }
        )
        CheckBoxPreference(
            text = stringResource(R.string.ui_settings_debugMode),
            secondaryText = stringResource(R.string.ui_settings_debugMode_description),
            onChange = { viewModel.setBooleanPreference("debug_mode", it) },
            defaultValue = viewModel.getBooleanPreference("debug_mode", false)
        )
    }
}

@Composable
fun PreferenceSelectApps(viewModel: MainViewModel) {
    var showSystem by remember { mutableStateOf(viewModel.getBooleanPreference("select_apps_showSystem", false)) }
    var showModule by remember { mutableStateOf(viewModel.getBooleanPreference("select_apps_showModule", false)) }
    val installedPackages = remember { viewModel.getAllInstalledPackages().sortedBy { it.appName }.sortedBy { !it.isForced } }
    viewModel.topAppBarActions.value = {
        SelectAppsToolbarAction(
            showSystem = showSystem, showModule = showModule,
            onChangeShowSystem = { showSystem = it; viewModel.setBooleanPreference("select_apps_showSystem", it) },
            onChangeShowModule = { showModule = it; viewModel.setBooleanPreference("select_apps_showSystem", it) }
        )
    }
    DisposableEffect(key1 = viewModel) {
        onDispose { viewModel.topAppBarActions.value = {} }
    }
    LazyColumn {
        items(
            key = { it.packageName },
            items = installedPackages.filter { (showSystem or !it.isSystem) and (showModule or !it.isModule) },
        ) { item ->
            CheckBoxPreference(
                text = item.appName,
                secondaryText = item.packageName,
                defaultValue = item.isForced,
                onChange = { viewModel.onChangeForcedApps(item.packageName, it) },
                icon = {
                    Image(
                        contentDescription = null,
                        painter = rememberDrawablePainter(item.appIcon),
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                    )
                }
            )
        }
    }
}