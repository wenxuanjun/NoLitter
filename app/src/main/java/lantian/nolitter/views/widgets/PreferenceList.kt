package lantian.nolitter.views.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PreferenceList(
    text: String, modifier: Modifier = Modifier,
    secondaryText: String? = null, icon: (@Composable () -> Unit)? = null,
    dialogTitle: String, options: Map<String, String>,
    defaultValue: String?, onSubmit: ((String) -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(defaultValue) }
    PreferenceDialog(
        text = text, icon = icon, showDialog = showDialog,
        secondaryText = secondaryText, modifier = modifier,
        onShowDialogChange = { showDialog = it }, dialogTitle = dialogTitle,
        dialogContent = {
            Column(Modifier.selectableGroup()) {
                options.forEach {
                    Row(
                        verticalAlignment = CenterVertically,
                        modifier = modifier.fillMaxWidth().height(48.dp).selectable(
                            selected = (it.key == selectedOption),
                            onClick = {
                                showDialog = false
                                selectedOption = it.key
                                if(onSubmit != null) onSubmit(it.key)
                            }
                        )
                    ) {
                        RadioButton(selected = (it.key == selectedOption), onClick = null, modifier = Modifier.padding(start = 24.dp))
                        Text(text = it.value, style = MaterialTheme.typography.bodyLarge.merge(), modifier = Modifier.padding(start = 16.dp))
                    }
                }
            }
        }
    )
}