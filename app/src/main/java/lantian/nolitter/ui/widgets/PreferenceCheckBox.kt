package lantian.nolitter.ui.widgets

import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PreferenceCheckBox(
    text: String, secondaryText: String? = null,
    modifier: Modifier = Modifier, defaultValue: Boolean = false,
    onChange: ((Boolean) -> Unit)? = null, icon: (@Composable () -> Unit)? = null
) {
    var checked by remember { mutableStateOf(defaultValue) }
    PreferenceBase(
        text = text, icon = icon,
        secondaryText = secondaryText,
        trailing = { Checkbox(checked = checked, onCheckedChange = null) },
        modifier = modifier.toggleable(value = checked, onValueChange = { checked = it; if (onChange != null) onChange(it) }),
    )
}