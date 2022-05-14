package lantian.nolitter.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import lantian.nolitter.Constants
import lantian.nolitter.R
import lantian.nolitter.models.InstalledPackageInfo
import lantian.nolitter.models.MainViewModel
import lantian.nolitter.ui.widgets.*

@Composable
fun PreferenceHome( viewModel: MainViewModel, navController: NavHostController) {
    Column {
        ClickablePreferenceItem(
            text = stringResource(R.string.ui_settings_general),
            secondaryText = stringResource(R.string.ui_settings_general_description),
            icon = { Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.padding(8.dp)) },
            onClick = { navController.navigate("PreferenceGeneral") }
        )
        ClickablePreferenceItem(
            text = stringResource(R.string.ui_settings_interface),
            secondaryText = stringResource(R.string.ui_settings_interface_description),
            icon = { Icon(imageVector = Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.padding(8.dp)) },
            onClick = { navController.navigate("PreferenceInterface") }
        )
        ClickablePreferenceItem(
            text = stringResource(R.string.ui_settings_miscellaneous),
            secondaryText = stringResource(R.string.ui_settings_miscellaneous_description),
            icon = { Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = null, modifier = Modifier.padding(8.dp)) },
            onClick = { navController.navigate("PreferenceMiscellaneous") }
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
        PreferenceCheckBox(
            text = stringResource(R.string.ui_settings_separateApp),
            secondaryText = stringResource(R.string.ui_settings_separateApp_description),
            onChange = { viewModel.setBooleanPreference("separate_app", it) },
            defaultValue = viewModel.getBooleanPreference("separate_app", true)
        )
        PreferenceCheckBox(
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
        val themeOptions = mapOf(
            Pair("default", stringResource(R.string.ui_settings_theme_default)),
            Pair("light", stringResource(R.string.ui_settings_theme_light)),
            Pair("dark", stringResource(R.string.ui_settings_theme_dark))
        )
        val selectedThemeKey = viewModel.getStringPreference("theme", "default")
        var selectedTheme by remember { mutableStateOf(themeOptions[selectedThemeKey]) }
        PreferenceList(
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
        PreferenceCheckBox(
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
fun PreferenceMiscellaneous(viewModel: MainViewModel) {
    Column {
        PreferenceGroup(stringResource(R.string.ui_settings_miscellaneous_advanced)) {
            PreferenceEditText(
                text = stringResource(R.string.ui_settings_miscellaneous_redirectDir),
                secondaryText = stringResource(R.string.ui_settings_miscellaneous_redirectDir_description),
                dialogTitle = stringResource(R.string.ui_settings_miscellaneous_redirectDir),
                dialogDefaultContent = viewModel.getStringPreference("redirect_dir", Constants.defaultRedirectDir),
                onSubmit = { viewModel.setStringPreference("redirect_dir", it) }
            )
            PreferenceCheckBox(
                text = stringResource(R.string.ui_settings_miscellaneous_debugMode),
                secondaryText = stringResource(R.string.ui_settings_miscellaneous_debugMode_description),
                onChange = { viewModel.setBooleanPreference("debug_mode", it) },
                defaultValue = viewModel.getBooleanPreference("debug_mode", false)
            )
        }
        PreferenceGroup(stringResource(R.string.ui_settings_miscellaneous_about)) {
            val sourceLink = stringResource(R.string.ui_settings_miscellaneous_source_description)
            val lantianLink = stringResource(R.string.ui_settings_miscellaneous_lantian_description)
            val xinternalsdLink = stringResource(R.string.ui_settings_miscellaneous_xinternalsd_description)
            ClickablePreferenceItem(
                text = stringResource(R.string.ui_settings_miscellaneous_source),
                secondaryText = sourceLink,
                onClick = { viewModel.intentToWebsite(sourceLink) }
            )
            ClickablePreferenceItem(
                text = stringResource(R.string.ui_settings_miscellaneous_lantian),
                secondaryText = lantianLink,
                onClick = { viewModel.intentToWebsite(lantianLink) }
            )
            ClickablePreferenceItem(
                text = stringResource(R.string.ui_settings_miscellaneous_xinternalsd),
                secondaryText = xinternalsdLink,
                onClick = { viewModel.intentToWebsite(xinternalsdLink) }
            )
        }
    }
}

@Composable
fun PreferenceSelectApps(viewModel: MainViewModel) {
    var hideSystem by remember { mutableStateOf(viewModel.getBooleanPreference("select_apps_hideSystem", true)) }
    var hideModule by remember { mutableStateOf(viewModel.getBooleanPreference("select_apps_hideModule", true)) }
    var sortedBy by remember { mutableStateOf(viewModel.getStringPreference("select_apps_sortedBy", "app_name")) }
    LaunchedEffect(key1 = true) { if (viewModel.installedPackages.value.isEmpty()) viewModel.initAllInstalledPackages() }
    DisposableEffect(key1 = viewModel) { onDispose { viewModel.topAppBarActions.value = {} } }
    if (viewModel.installedPackages.value.isNotEmpty()) {
        viewModel.topAppBarActions.value = {
            SelectAppsToolbarAction(
                hideSystem = hideSystem, hideModule = hideModule, sortedBy = sortedBy,
                onChangeHideSystem = { hideSystem = it; viewModel.setBooleanPreference("select_apps_showSystem", it) },
                onChangeHideModule = { hideModule = it; viewModel.setBooleanPreference("select_apps_showSystem", it) },
                onChangeSortedBy =  { sortedBy = it; viewModel.setStringPreference("select_apps_sortedBy", it) }
            )
        }
        LazyColumn {
            items(
                key = { it.packageName },
                items = viewModel.installedPackages.value
                    .filter { (!hideSystem or !it.isSystem) and (!hideModule or !it.isModule) }
                    .sortedWith(
                        compareByDescending<InstalledPackageInfo> { it.isForced }
                            .thenBy { if (sortedBy == "app_name") it.appName else true }
                            .thenByDescending { if (sortedBy == "first_install_time") it.firstInstallTime else true }
                    )
            ) { item ->
                PreferenceCheckBox(
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
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
            Text(stringResource(R.string.ui_loading))
        }
    }
}