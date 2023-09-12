package lantian.nolitter.views.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import lantian.nolitter.LocalActivity
import lantian.nolitter.R
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.model.PackageViewModel
import lantian.nolitter.views.widgets.PreferenceCheckBox
import lantian.nolitter.views.widgets.PreferenceClickableItem
import lantian.nolitter.views.widgets.PreferenceGroup
import lantian.nolitter.views.widgets.PreferenceList

@Composable
fun PackagePreference(
    packageName: String,
    packageViewModel: PackageViewModel,
    viewModel: MainViewModel = hiltViewModel(LocalActivity.current),
    navigateToCustomRedirect: (String) -> Unit
) {
    LaunchedEffect(true) {
        viewModel.appBarContent = viewModel.appBarContent.copy(
            title = { Text(packageViewModel.getPackageInfo(packageName).appName) }
        )
        packageViewModel.getPackagePreference(packageName)
    }
    packageViewModel.currentPackagePreference?.let { packagePreference ->
        Column {
            PreferenceGroup("General") {
                PreferenceCheckBox(
                    text = stringResource(R.string.ui_settings_forcedMode),
                    secondaryText = stringResource(R.string.ui_settings_forcedMode_description),
                    defaultValue = packagePreference.forcedMode,
                    onChange = { packageViewModel.setPackagePreference(packagePreference.copy(forcedMode = it)) }
                )
                PreferenceCheckBox(
                    text = stringResource(R.string.ui_settings_allowPublicDirs),
                    secondaryText = stringResource(R.string.ui_settings_allowPublicDirs_description),
                    defaultValue = packagePreference.allowPublicDirs,
                    onChange = { packageViewModel.setPackagePreference(packagePreference.copy(allowPublicDirs = it)) }
                )
                PreferenceCheckBox(
                    text = stringResource(R.string.ui_settings_additionalHooks),
                    secondaryText = stringResource(R.string.ui_settings_additionalHooks_description),
                    defaultValue = packagePreference.additionalHooks,
                    onChange = { packageViewModel.setPackagePreference(packagePreference.copy(additionalHooks = it)) }
                )
                PreferenceList(
                    text = stringResource(R.string.ui_settings_redirectStyle),
                    secondaryText = stringResource(R.string.ui_settings_redirectStyle_description),
                    dialogTitle = stringResource(R.string.ui_settings_redirectStyle),
                    options = mapOf("data" to "Data", "cache" to "Cache", "external" to "External"),
                    defaultValue = packagePreference.redirectStyle,
                    onSubmit = { packageViewModel.setPackagePreference(packagePreference.copy(redirectStyle = it)) }
                )
            }
            PreferenceGroup("Advanced") {
                PreferenceClickableItem(
                    text = "Customize Redirect",
                    secondaryText = "Redirects specified files or directories to a custom location",
                    onClick = { navigateToCustomRedirect(packageName) },
                )
            }
        }
    }
}
