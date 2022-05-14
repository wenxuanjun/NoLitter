package lantian.nolitter.ui.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PreferenceDialog(
    text: String, icon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier, secondaryText: String? = null,
    showDialog: Boolean, onShowDialogChange: (Boolean) -> Unit, dialogTitle: String? = null,
    dialogContent: (@Composable () -> Unit)? = null, dialogActions: (@Composable (Boolean) -> Unit)? = null
) {
    PreferenceBase(
        text = text, icon = icon, secondaryText = secondaryText,
        modifier = modifier.toggleable(value = showDialog, onValueChange = onShowDialogChange),
    )
    if (showDialog) {
        Dialog(onDismissRequest = { onShowDialogChange(false) }) {
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface) {
                Column {
                    if (dialogTitle != null) {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Text(text = dialogTitle, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                    if (dialogContent != null) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) { dialogContent() }
                    }
                    if (dialogActions != null) {
                        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) { dialogActions(showDialog) }
                        }
                    }
                }
            }
        }
    }
}