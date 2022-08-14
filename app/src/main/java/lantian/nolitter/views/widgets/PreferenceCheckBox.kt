package lantian.nolitter.views.widgets

import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PreferenceCheckBox(
    text: String, modifier: Modifier = Modifier,
    disabled: Boolean = false, secondaryText: String? = null, defaultValue: Boolean = false,
    onChange: ((Boolean) -> Unit)? = null, icon: (@Composable () -> Unit)? = null
) {
    var checked by remember { mutableStateOf(defaultValue) }
    Text(defaultValue.toString())
    PreferenceBase(
        text = text, icon = icon, secondaryText = secondaryText,
        trailing = { Checkbox(checked = checked, onCheckedChange = null) },
        modifier = if (disabled) modifier.alpha(0.5f) else modifier.toggleable(
            value = checked,
            onValueChange = { checked = it; if (onChange != null) onChange(it) }
        )
    )
}