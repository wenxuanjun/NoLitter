package lantian.nolitter.views.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import lantian.nolitter.views.models.MainViewModel
import lantian.nolitter.views.models.PackageViewModel
import lantian.nolitter.views.widgets.PreferenceCheckBox
import lantian.nolitter.views.widgets.PreferenceList

@Composable
fun PackagePreference(
    packageName: String, viewModel: MainViewModel,
    packageViewModel: PackageViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        packageViewModel.getPackagePreference(packageName)
    }
    packageViewModel.currentPackagePreference?.let { packagePreference ->
        Column {
            Text(packagePreference.toString())
            PreferenceCheckBox(
                text = "Forced mode",
                secondaryText = "Several Texts",
                defaultValue = packagePreference.forcedMode,
                onChange = { packageViewModel.setPackagePreference(packagePreference.copy(forcedMode = it)) }
            )
            PreferenceCheckBox(
                text = "Allow access to standard directories",
                secondaryText = "Several Texts",
                defaultValue = packagePreference.allowStandardDirs,
                onChange = { packageViewModel.setPackagePreference(packagePreference.copy(allowStandardDirs = it)) }
            )
            PreferenceCheckBox(
                text = "Enable additional hooks",
                secondaryText = "Several Texts",
                defaultValue = packagePreference.additionalHooks,
                onChange = { packageViewModel.setPackagePreference(packagePreference.copy(additionalHooks = it)) }
            )
            PreferenceList(
                text = "Redirect style",
                secondaryText = "Several Texts",
                dialogTitle = "Redirect style",
                options = mapOf("data" to "Data", "cache" to "Cache", "external" to "External"),
                defaultValue = packagePreference.redirectStyle,
                onSubmit = { packageViewModel.setPackagePreference(packagePreference.copy(redirectStyle = it)) }
            )
        }
    }
}