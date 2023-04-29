package lantian.nolitter.views.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import lantian.nolitter.R
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.widgets.PreferenceCheckBox
import lantian.nolitter.views.widgets.PreferenceClickableItem
import lantian.nolitter.views.widgets.PreferenceGroup

@Composable
fun Miscellaneous(viewModel: MainViewModel) {
    Column {
        PreferenceGroup(stringResource(R.string.ui_settings_miscellaneous_advanced)) {
            PreferenceCheckBox(
                text = stringResource(R.string.ui_settings_miscellaneous_debugMode),
                secondaryText = stringResource(R.string.ui_settings_miscellaneous_debugMode_description),
                onChange = { viewModel.setPreference("debug_mode", it) },
                defaultValue = viewModel.getPreference("debug_mode", false)
            )
        }
        PreferenceGroup(stringResource(R.string.ui_settings_miscellaneous_about)) {
            val context = LocalContext.current
            val sourceLink = stringResource(R.string.ui_settings_miscellaneous_source_description)
            val lantianLink = stringResource(R.string.ui_settings_miscellaneous_lantian_description)
            PreferenceClickableItem(
                text = stringResource(R.string.ui_settings_miscellaneous_source),
                secondaryText = sourceLink,
                onClick = { MainViewModel.intentToWebsite(context, sourceLink) }
            )
            PreferenceClickableItem(
                text = stringResource(R.string.ui_settings_miscellaneous_lantian),
                secondaryText = lantianLink,
                onClick = { MainViewModel.intentToWebsite(context, lantianLink) }
            )
        }
    }
}