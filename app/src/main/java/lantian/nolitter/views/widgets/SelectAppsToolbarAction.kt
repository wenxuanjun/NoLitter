package lantian.nolitter.views.widgets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import lantian.nolitter.R
import lantian.nolitter.repository.ProcessPackageInfoPreference
import lantian.nolitter.views.model.PackageViewModel

@Composable
fun SelectAppsToolbarAction(
    packageViewModel: PackageViewModel,
    processPreference: ProcessPackageInfoPreference,
    onProcessPreferenceChange: (ProcessPackageInfoPreference) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    IconButton(onClick = { showMenu = !showMenu }) {
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
    }
    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
        PreferenceGroup(stringResource(R.string.ui_settings_packageList_hide)) {
            CheckBoxDropdownMenuItem(
                text = stringResource(R.string.ui_settings_packageList_hide_system),
                checked = processPreference.hideSystem,
                onShowMenuChange = { showMenu = false },
                onCheckedChange = {
                    onProcessPreferenceChange(processPreference.copy(hideSystem = it))
                    packageViewModel.setPreference("select_hideSystem", it)
                }
            )
            CheckBoxDropdownMenuItem(
                text = stringResource(R.string.ui_settings_packageList_hide_module),
                checked = processPreference.hideModule,
                onShowMenuChange = { showMenu = false },
                onCheckedChange = {
                    onProcessPreferenceChange(processPreference.copy(hideModule = it))
                    packageViewModel.setPreference("select_hideModule", it)
                }
            )
        }
        PreferenceGroup(stringResource(R.string.ui_settings_packageList_sort)) {
            RadioButtonDropdownMenuItem(
                text = stringResource(R.string.ui_settings_packageList_sort_appName),
                selected = (processPreference.sortedBy == "app_name"),
                onShowMenuChange = { showMenu = false },
                onSelectedChange = {
                    onProcessPreferenceChange(processPreference.copy(sortedBy = "app_name"))
                    packageViewModel.setPreference("select_sortedBy", "app_name")
                }
            )
            RadioButtonDropdownMenuItem(
                text = stringResource(R.string.ui_settings_packageList_sort_firstInstallTime),
                selected = (processPreference.sortedBy == "first_install_time"),
                onShowMenuChange = { showMenu = false },
                onSelectedChange = {
                    onProcessPreferenceChange(processPreference.copy(sortedBy = "first_install_time"))
                    packageViewModel.setPreference("select_sortedBy", "first_install_time")
                }
            )
        }
    }
}

@Composable
fun CheckBoxDropdownMenuItem(
    text: String, checked: Boolean,
    onShowMenuChange: () -> Unit, onCheckedChange: (Boolean) -> Unit,
) {
    DropdownMenuItem(
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.width(200.dp),
        onClick = { onShowMenuChange(); onCheckedChange(!checked) },
        trailingIcon = { Checkbox(checked = checked, onCheckedChange = null, modifier = Modifier.padding(end = 16.dp)) },
        text = { Text(text = text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 16.dp)) }
    )
}

@Composable
fun RadioButtonDropdownMenuItem(
    text: String, selected: Boolean,
    onShowMenuChange: () -> Unit, onSelectedChange: () -> Unit,
) {
    DropdownMenuItem(
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.width(200.dp),
        onClick = { onShowMenuChange(); onSelectedChange() },
        trailingIcon = { RadioButton(selected = selected, onClick = null, modifier = Modifier.padding(end = 16.dp)) },
        text = { Text(text = text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 16.dp)) }
    )
}