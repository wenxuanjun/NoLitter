package lantian.nolitter.views.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import lantian.nolitter.repository.InstalledPackageInfo
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.model.PackageViewModel
import lantian.nolitter.views.widgets.LoadingScreen
import lantian.nolitter.views.widgets.PreferenceClickableCheckbox
import lantian.nolitter.views.widgets.SelectAppsToolbarAction

@Composable
fun PackageList(
    navController: NavController, viewModel: MainViewModel,
    packageViewModel: PackageViewModel
) {
    DisposableEffect(key1 = viewModel) {
        onDispose { viewModel.topAppBarActions.value = {} }
    }
    if (packageViewModel.packageInfo.isEmpty()) { LoadingScreen() } else {
        val defaultProcessData = packageViewModel.getDefaultProcessPreference()
        var sortedBy by remember { mutableStateOf(defaultProcessData.sortedBy) }
        var hideSystem by remember { mutableStateOf(defaultProcessData.hideSystem) }
        var hideModule by remember { mutableStateOf(defaultProcessData.hideModule) }
        viewModel.topAppBarActions.value = { SelectAppsToolbarAction(
            hideSystem = hideSystem, hideModule = hideModule, sortedBy = sortedBy,
            onChangeHideSystem = { hideSystem = it; packageViewModel.setPreference("select_hideSystem", it) },
            onChangeHideModule = { hideModule = it; packageViewModel.setPreference("select_hideModule", it) },
            onChangeSortedBy =  { sortedBy = it; packageViewModel.setPreference("select_sortedBy", it) }
        ) }
        LazyColumn {
            val filteredItems = packageViewModel.packageInfo
                .filter { (!hideSystem or !it.isSystem) and (!hideModule or !it.isModule) }
                .sortedWith(compareByDescending<InstalledPackageInfo> { packageViewModel.customizedPackages.contains(it.packageName) }
                    .thenBy { if (sortedBy == "app_name") it.appName else true }
                    .thenByDescending { if (sortedBy == "first_install_time") it.firstInstallTime else true }
                )
            items(key = { it.packageName }, items = filteredItems) { item ->
                PreferenceClickableCheckbox(
                    text = item.appName, secondaryText = item.packageName, defaultValue = packageViewModel.isCustomizedPackages(item.packageName),
                    onClick = { navController.navigate("package/${item.packageName}") },
                    onChange = { packageViewModel.onChangeCustomizedPackages(item.packageName, it) },
                    icon = {
                        Image(
                            painter = rememberDrawablePainter(item.appIcon),
                            contentDescription = null, modifier = Modifier.size(36.dp).clip(CircleShape)
                        )
                    }
                )
            }
        }
    }
}