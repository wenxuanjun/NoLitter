package lantian.nolitter.views.widgets

import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun PreferenceClickableCheckbox(
    text: String, modifier: Modifier = Modifier,
    secondaryText: String? = null, defaultValue: Boolean = false,
    onChange: ((Boolean) -> Unit)? = null, onClick: (() -> Unit)? = null, icon: (@Composable () -> Unit)? = null
) {
    var checked by remember { mutableStateOf(defaultValue) }
    PreferenceBase(
        text = text, icon = icon, secondaryText = secondaryText,
        trailing = { Checkbox(checked = checked, onCheckedChange = { checked = it; if (onChange != null) onChange(it) }) },
        modifier = modifier.alpha(if (checked) 1f else 0.5f).clickable(enabled = checked, onClick = { if (onClick != null) onClick() })
    )
}