package lantian.nolitter.views.screens

import androidx.compose.runtime.Composable

@Composable
fun PackagePreference(
    packageName: String,
    //viewModel: PackageViewModel = PackageViewModel()
) {
    /*viewModel.packagePreference.value.let {
        Column {
            Text(packageName)
            PreferenceCheckBox(
                text = "Forced mode",
                secondaryText = "Several Texts",
                defaultValue = it.forcedMode,
                onChange = { viewModel.setPackagePreference("forced_mode", it) }
            )
            PreferenceCheckBox(
                text = "Allow access to standard directories",
                secondaryText = "Several Texts",
                defaultValue = it.allowStandardDir,
                onChange = { viewModel.setPackagePreference("allow_standard_dir", it) }
            )
            PreferenceCheckBox(
                text = "Enable additional hooks",
                secondaryText = "Several Texts",
                defaultValue = it.additionalHooks,
                onChange = { viewModel.setPackagePreference("additional_hooks", it) }
            )
            PreferenceList(
                text = "Redirect style",
                secondaryText = "Several Texts",
                dialogTitle = "Redirect style",
                options = mapOf("data" to "Data", "cache" to "Cache", "external" to "External"),
                defaultValue = it.redirectStyle,
                onSubmit = { viewModel.setPackagePreference("redirect_style", it) }
            )
        }
    }*/
}