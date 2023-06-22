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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import lantian.nolitter.R
import lantian.nolitter.repository.InstalledPackageInfo
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.model.PackageViewModel
import lantian.nolitter.views.widgets.AppBarTextField
import lantian.nolitter.views.widgets.LoadingScreen
import lantian.nolitter.views.widgets.PreferenceClickableCheckbox
import lantian.nolitter.views.widgets.SelectAppsToolbarAction

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun PackageList(
    navController: NavController,
    viewModel: MainViewModel,
    packageViewModel: PackageViewModel
) {
    // The packageInfo will be empty until it is loaded
    if (packageViewModel.packageInfo.isEmpty()) { LoadingScreen() } else {

        // Initialize the process preference
        val defaultProcessPreference = packageViewModel.getDefaultProcessPreference()
        val (processPreference, setProcessPreference) = remember { mutableStateOf(defaultProcessPreference) }

        // Need to remember the title so that it can be restored when the search is closed
        val rememberedTitle = remember { viewModel.topAppBarContent.title }

        // Initialize the search text field
        var searchEnabled by remember { mutableStateOf(false) }
        val (searchText, setSearchText) = remember { mutableStateOf("") }
        val onSearchClosed = {
            searchEnabled = false; setSearchText("")
            viewModel.topAppBarContent = viewModel.topAppBarContent.copy(title = rememberedTitle)
        }

        val onSearchIconClick = {
            searchEnabled = true
            viewModel.topAppBarContent = viewModel.topAppBarContent.copy(
                title = {
                    AppBarTextField(
                        value = searchText,
                        onValueChange = setSearchText,
                        placeholder = { Text(stringResource(R.string.ui_search)) },
                        onClose = onSearchClosed
                    )
                }
            )
        }

        // Initialize the toolbar actions
        viewModel.topAppBarContent = viewModel.topAppBarContent.copy(
            actions = {
                if (!searchEnabled) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onSearchIconClick() }
                    )
                }
                SelectAppsToolbarAction(
                    packageViewModel = packageViewModel,
                    processPreference = processPreference,
                    onProcessPreferenceChange = setProcessPreference
                )
            }
        )

        LazyColumn {
            val filteredItems = packageViewModel.packageInfo
                .filter {
                    if (searchEnabled) {
                        it.appName.contains(searchText, true) or it.packageName.contains(searchText, true)
                    } else { true }
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