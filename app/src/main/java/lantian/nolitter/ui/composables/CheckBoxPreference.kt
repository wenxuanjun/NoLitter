package lantian.nolitter.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CheckBoxPreference(
    text: String, secondaryText: String? = null, modifier: Modifier = Modifier,
    defaultValue: Boolean = false, onChange: ((Boolean) -> Unit)? = null, icon: (@Composable () -> Unit)? = null
) {
    var checked by remember { mutableStateOf(defaultValue) }
    BasePreference(
        text = text, icon = icon,
        secondaryText = secondaryText,
        trailing = { Checkbox(checked = checked, onCheckedChange = null) },
        modifier = modifier.toggleable(value = checked, onValueChange = { checked = it; if (onChange != null) onChange(it) }),
    )
}