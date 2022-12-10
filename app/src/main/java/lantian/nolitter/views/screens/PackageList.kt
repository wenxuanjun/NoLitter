package lantian.nolitter.views.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import lantian.nolitter.R
import lantian.nolitter.repository.InstalledPackageInfo
import lantian.nolitter.repository.ProcessPackageInfoPreference
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.model.PackageViewModel
import lantian.nolitter.views.widgets.AppBarTextField
import lantian.nolitter.views.widgets.LoadingScreen
import lantian.nolitter.views.widgets.PreferenceClickableCheckbox
import lantian.nolitter.views.widgets.SelectAppsToolbarAction

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun PackageList(
    navController: NavController, viewModel: MainViewModel,
    packageViewModel: PackageViewModel
) {
    // The packageInfo will be empty until it is loaded
    if (packageViewModel.packageInfo.isEmpty()) { LoadingScreen() } else {

        // Initialize the process preference
        val defaultProcessPreference = packageViewModel.getDefaultProcessPreference()
        val (processPreference, setProcessPreference) = remember { mutableStateOf(defaultProcessPreference) }

        // Initialize the search text field
        var searchEnabled by remember { mutableStateOf(false) }
        val (searchText, setSearchText) = remember { mutableStateOf("") }
        val disableSearch = {
            searchEnabled = false; setSearchText("")
            viewModel.topAppBarContent = viewModel.topAppBarContent.copy(actions = {}, isTitleCompose = false)
        }

        // Set the actions of the app bar
        viewModel.topAppBarContent = viewModel.topAppBarContent.copy(
            actions = {
                PackageListTopBarActions(
                    packageViewModel = packageViewModel,
                    processPreference = processPreference,
                    onProcessPreferenceChange = setProcessPreference,
                    searchEnabled = searchEnabled,
                    onSearchIconClick = {
                        searchEnabled = true
                        viewModel.topAppBarContent = viewModel.topAppBarContent.copy(
                            titleCompose = {
                                AppBarTextField(
                                    value = searchText,
                                    onValueChange = setSearchText,
                                    onClose = disableSearch,
                                    placeholder = { Text(stringResource(R.string.ui_search)) }
                                )
                            },
                            isTitleCompose = true
                        )
                    }
                )
            }
        )

        // Remove the search text field when navigating out
        DisposableEffect(packageViewModel) { onDispose { disableSearch() } }

        LazyColumn {
            val filteredItems = packageViewModel.packageInfo
                .filter {
                    if (searchEnabled) { it.appName.contains(searchText, true) } else true
                }
                .filter {
                    (!processPreference.hideSystem or !it.isSystem) and (!processPreference.hideModule or !it.isModule)
                }
                .sortedWith(
                    // Customized packages will be shown at the top
                    compareByDescending<InstalledPackageInfo> { packageViewModel.customizedPackages.contains(it.packageName) }
                    .thenBy { if (processPreference.sortedBy == "app_name") it.appName else true }
                    .thenByDescending { if (processPreference.sortedBy == "first_install_time") it.firstInstallTime else true }
                )

            // Show the filtered items
            items(key = { it.packageName }, items = filteredItems) { item ->
                PreferenceClickableCheckbox(
                    text = item.appName, secondaryText = item.packageName,
                    defaultValue = packageViewModel.isCustomizedPackages(item.packageName),
                    onClick = { navController.navigate("package/${item.packageName}") },
                    onChange = { packageViewModel.onChangeCustomizedPackages(item.packageName, it) },
                    modifier = Modifier.animateItemPlacement(),
                    icon = {
                        Image(
                            painter = rememberDrawablePainter(item.appIcon),
                            contentDescription = null, modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun PackageListTopBarActions(
    packageViewModel: PackageViewModel,
    processPreference: ProcessPackageInfoPreference,
    onProcessPreferenceChange: (ProcessPackageInfoPreference) -> Unit,
    searchEnabled: Boolean,
    onSearchIconClick: () -> Unit
) {
    if (!searchEnabled) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.padding(8.dp).clickable { onSearchIconClick() }
        )
    }
    SelectAppsToolbarAction(
        hideSystem = processPreference.hideSystem,
        hideModule = processPreference.hideModule,
        sortedBy = processPreference.sortedBy,
        onChangeHideSystem = {
            onProcessPreferenceChange(processPreference.copy(hideSystem = it))
            packageViewModel.setPreference("select_hideSystem", it)
        },
        onChangeHideModule = {
            onProcessPreferenceChange(processPreference.copy(hideModule = it))
            packageViewModel.setPreference("select_hideModule", it)
        },
        onChangeSortedBy = {
            onProcessPreferenceChange(processPreference.copy(sortedBy = it))
            packageViewModel.setPreference("select_sortedBy", it)
        }
    )
}