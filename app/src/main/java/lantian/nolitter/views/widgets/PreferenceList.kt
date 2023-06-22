package lantian.nolitter.views.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceList(
    text: String,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    icon: (@Composable () -> Unit)? = null,
    dialogTitle: String,
    options: Map<String, String>,
    defaultValue: String?,
    onSubmit: ((String) -> Unit)? = null,
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(defaultValue) }

    PreferenceDialog(text = text,
        icon = icon,
        showDialog = showDialog,
        secondaryText = secondaryText,
        modifier = modifier,
        onShowDialogChange = { showDialog = it },
        dialogTitle = dialogTitle,
        dialogContent = {
            Column(Modifier.selectableGroup()) {
                options.forEach {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .selectable(
                                selected = (it.key == selectedOption),
                                onClick = {
                                    showDialog = false
                                    selectedOption = it.key
                                    if (onSubmit != null) onSubmit(it.key)
                                }
                            ),
                        verticalAlignment = CenterVertically,
                    ) {
                        RadioButton(
                            selected = (it.key == selectedOption),
                            onClick = null,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                        Text(
                            text = it.value,
                            style = MaterialTheme.typography.bodyLarge.merge(),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    )
}