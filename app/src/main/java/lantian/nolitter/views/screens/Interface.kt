package lantian.nolitter.views.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import lantian.nolitter.R
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.widgets.PreferenceCheckBox
import lantian.nolitter.views.widgets.PreferenceList

@Composable
fun Interface(viewModel: MainViewModel) {
    Column {
        val themeOptions = mapOf(
            Pair("default", stringResource(R.string.ui_settings_theme_default)),
            Pair("light", stringResource(R.string.ui_settings_theme_light)),
            Pair("dark", stringResource(R.string.ui_settings_theme_dark))
        )
        val selectedThemeKey = viewModel.getPreference("theme", "default")
        var selectedTheme by remember { mutableStateOf(themeOptions[selectedThemeKey]) }
        PreferenceList(
            text = stringResource(R.string.ui_settings_theme),
            secondaryText = selectedTheme,
            dialogTitle = stringResource(R.string.ui_settings_theme),
            defaultValue = selectedThemeKey,
            options = themeOptions
        ) {
            viewModel.setPreference("theme", it)
            selectedTheme = themeOptions[it]
            viewModel.appTheme.value = it
        }
        PreferenceCheckBox(
            text = stringResource(R.string.ui_settings_hideIcon),
            secondaryText = stringResource(R.string.ui_settings_hideIcon_description),
            defaultValue = viewModel.getPreference("hide_icon", false),
            onChange = {
                viewModel.setPreference("hide_icon", it)
                viewModel.hideAppIcon(it)
            }
        )
    }
}