package lantian.nolitter.views.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import lantian.nolitter.R
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.widgets.PreferenceCheckBox
import lantian.nolitter.views.widgets.PreferenceList

@Composable
fun General(viewModel: MainViewModel = hiltViewModel()) {
    Column {
        PreferenceCheckBox(
            text = stringResource(R.string.ui_settings_forcedMode),
            secondaryText = stringResource(R.string.ui_settings_forcedMode_description),
            defaultValue = viewModel.getPreference("forced_mode", false),
            onChange = { viewModel.setPreference("forced_mode", it) }
        )
        PreferenceCheckBox(
            text = stringResource(R.string.ui_settings_allowPublicDirs),
            secondaryText = stringResource(R.string.ui_settings_allowPublicDirs_description),
            defaultValue = viewModel.getPreference("allow_public_dirs", true),
            onChange = { viewModel.setPreference("allow_public_dirs", it) }
        )
        PreferenceCheckBox(
            text = stringResource(R.string.ui_settings_additionalHooks),
            secondaryText = stringResource(R.string.ui_settings_additionalHooks_description),
            defaultValue = viewModel.getPreference("additional_hooks", true),
            onChange = { viewModel.setPreference("additional_hooks", it) }
        )
        PreferenceList(
            text = stringResource(R.string.ui_settings_redirectStyle),
            secondaryText = stringResource(R.string.ui_settings_redirectStyle_description),
            dialogTitle = stringResource(R.string.ui_settings_redirectStyle),
            options = mapOf("data" to "Data", "cache" to "Cache", "external" to "External"),
            defaultValue = viewModel.getPreference("redirect_style", "data"),
            onSubmit = { viewModel.setPreference("redirect_style", it) }
        )
    }
}