package lantian.nolitter.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import lantian.nolitter.R
import lantian.nolitter.view.model.InstalledPackageInfo
import lantian.nolitter.view.model.MainViewModel
import lantian.nolitter.view.widgets.*

@Composable
fun PreferenceHome(navController: NavHostController) {
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
fun PreferenceGeneral(navController: NavHostController) {
    Column {
        ClickablePreferenceItem(
            text = stringResource(R.string.ui_settings_forceMode),
            secondaryText = stringResource(R.string.ui_settings_forceMode_description),
            onClick = { navController.navigate("PreferenceSelectApps") }
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
        val selectedThemeKey = viewModel.dataStore.getPreference("theme", "default")
        var selectedTheme by remember { mutableStateOf(themeOptions[selectedThemeKey]) }
        PreferenceList(
            text = stringResource(R.string.ui_settings_theme),
            secondaryText = selectedTheme,
            dialogTitle = stringResource(R.string.ui_settings_theme),
            defaultValue = selectedThemeKey,
            options = themeOptions
        ) {
            viewModel.dataStore.setPreference("theme", it)
            selectedTheme = themeOptions[it]
            viewModel.appTheme.value = it
        }
        PreferenceCheckBox(
            text = stringResource(R.string.ui_settings_hideIcon),
            secondaryText = stringResource(R.string.ui_settings_hideIcon_description),
            defaultValue = viewModel.dataStore.getPreference("hide_icon", false),
            onChange = {
                viewModel.dataStore.setPreference("hide_icon", it)
                viewModel.hideAppIcon(it)
            }
        )
    }
}

@Composable
fun PreferenceMiscellaneous(viewModel: MainViewModel) {
    Column {
        PreferenceGroup(stringResource(R.string.ui_settings_miscellaneous_advanced)) {
            PreferenceCheckBox(
                text = stringResource(R.string.ui_settings_miscellaneous_debugMode),
                secondaryText = stringResource(R.string.ui_settings_miscellaneous_debugMode_description),
                onChange = { viewModel.dataStore.setPreference("debug_mode", it) },
                defaultValue = viewModel.dataStore.getPreference("debug_mode", false)
            )
        }
        PreferenceGroup(stringResource(R.string.ui_settings_miscellaneous_about)) {
            val sourceLink = stringResource(R.string.ui_settings_miscellaneous_source_description)
            val lantianLink = stringResource(R.string.ui_settings_miscellaneous_lantian_description)
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
        }
    }
}

@Composable
fun PreferenceSelectApps(viewModel: MainViewModel) {
    val defaultProcessData = viewModel.getDefaultProcessPreference()
    var preferencesChanged by remember { mutableStateOf(false) }
    var sortedBy by remember { mutableStateOf(defaultProcessData.sortedBy) }
    var hideSystem by remember { mutableStateOf(defaultProcessData.hideSystem) }
    var hideModule by remember { mutableStateOf(defaultProcessData.hideModule) }
    LaunchedEffect(key1 = true) { viewModel.initAllInstalledPackages() }
    DisposableEffect(key1 = viewModel) { onDispose {
        viewModel.topAppBarActions.value = {}
        if (preferencesChanged) viewModel.initAllInstalledPackages()
    } }
    if (viewModel.installedPackages.value.isEmpty()) { LoadingScreen() } else {
        viewModel.topAppBarActions.value = { SelectAppsToolbarAction(
            hideSystem = hideSystem, hideModule = hideModule, sortedBy = sortedBy,
            onChangeHideSystem = { hideSystem = it; viewModel.dataStore.setPreference("select_hideSystem", it) },
            onChangeHideModule = { hideModule = it; viewModel.dataStore.setPreference("select_hideModule", it) },
            onChangeSortedBy =  { sortedBy = it; viewModel.dataStore.setPreference("select_sortedBy", it) }
        ) }
        LazyColumn {
            val filteredItems = viewModel.installedPackages.value
                .filter { (!hideSystem or !it.isSystem) and (!hideModule or !it.isModule) }
                .sortedWith(compareByDescending<InstalledPackageInfo> { it.isForced }
                    .thenBy { if (sortedBy == "app_name") it.appName else true }
                    .thenByDescending { if (sortedBy == "first_install_time") it.firstInstallTime else true }
                )
            items(key = { it.packageName }, items = filteredItems) { item ->
                PreferenceCheckBox(
                    text = item.appName, secondaryText = item.packageName, defaultValue = item.isForced,
                    onChange = { viewModel.onChangeForcedApps(item.packageName, it); preferencesChanged = true },
                    icon = { Image(
                        painter = rememberDrawablePainter(item.appIcon),
                        contentDescription = null, modifier = Modifier.size(36.dp).clip(CircleShape)
                    ) }
                )
            }
        }
    }
}