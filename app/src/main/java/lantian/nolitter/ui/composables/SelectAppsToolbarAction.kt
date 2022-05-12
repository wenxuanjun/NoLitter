package lantian.nolitter.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import lantian.nolitter.R

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun SelectAppsToolbarAction(
    showSystem: Boolean, showModule: Boolean,
    onChangeShowSystem: (Boolean) -> Unit, onChangeShowModule: (Boolean) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    IconButton(onClick = { showMenu = !showMenu }) {
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
    }
    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
        DropdownMenuItem(
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.width(180.dp),
            onClick = { onChangeShowSystem(!showSystem); showMenu = false },
        ) {
            ListItem(
                text = { Text(stringResource(R.string.ui_settings_forceMode_showSystem)) },
                trailing = { Checkbox(checked = showSystem, onCheckedChange = null) },
            )
        }
        DropdownMenuItem(
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.width(180.dp),
            onClick = { onChangeShowModule(!showModule); showMenu = false }
        ) {
            ListItem(
                text = { Text(stringResource(R.string.ui_settings_forceMode_showModule)) },
                trailing = { Checkbox(checked = showModule, onCheckedChange = null) },
            )
        }
    }
}