package lantian.nolitter.views.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import lantian.nolitter.R
import lantian.nolitter.views.widgets.PreferenceClickableItem

@Composable
fun General(navController: NavHostController) {
    Column {
        PreferenceClickableItem(
            text = stringResource(R.string.ui_settings_forceMode),
            secondaryText = stringResource(R.string.ui_settings_forceMode_description),
            onClick = { navController.navigate("packages") }
        )
    }
}