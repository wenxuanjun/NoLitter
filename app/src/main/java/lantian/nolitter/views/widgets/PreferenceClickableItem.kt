package lantian.nolitter.views.widgets

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PreferenceClickableItem(
    text: String, modifier: Modifier = Modifier, secondaryText: String? = null,
    onClick: (() -> Unit)? = null, icon: (@Composable () -> Unit)? = null
) {
    PreferenceBase(
        text = text, icon = icon, secondaryText = secondaryText,
        modifier = modifier.clickable { if (onClick != null) onClick() }
    )
}