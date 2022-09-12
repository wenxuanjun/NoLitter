package lantian.nolitter.views.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import lantian.nolitter.R
import lantian.nolitter.views.widgets.PreferenceClickableItem

@Composable
fun Home(navController: NavHostController) {
    Column {
        PreferenceClickableItem(
            text = stringResource(R.string.ui_settings_general),
            secondaryText = stringResource(R.string.ui_settings_general_description),
            icon = { Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.padding(8.dp)) },
            onClick = { navController.navigate("general") }
        )
        PreferenceClickableItem(
            text = stringResource(R.string.ui_settings_packages),
            secondaryText = stringResource(R.string.ui_settings_packages_description),
            icon = { Icon(imageVector = Icons.Default.AppRegistration, contentDescription = null, modifier = Modifier.padding(8.dp)) },
            onClick = { navController.navigate("packages") }
        )
        PreferenceClickableItem(
            text = stringResource(R.string.ui_settings_interface),
            secondaryText = stringResource(R.string.ui_settings_interface_description),
            icon = { Icon(imageVector = Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.padding(8.dp)) },
            onClick = { navController.navigate("interface") }
        )
        PreferenceClickableItem(
            text = stringResource(R.string.ui_settings_miscellaneous),
            secondaryText = stringResource(R.string.ui_settings_miscellaneous_description),
            icon = { Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = null, modifier = Modifier.padding(8.dp)) },
            onClick = { navController.navigate("miscellaneous") }
        )
    }
}