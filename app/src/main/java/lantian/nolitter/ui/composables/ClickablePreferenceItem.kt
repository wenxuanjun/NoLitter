package lantian.nolitter.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ClickablePreferenceItem(
    text: String, secondaryText: String? = null, modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null, icon: (@Composable () -> Unit)? = null
) {
    BasePreference(
        text = text, icon = icon, secondaryText = secondaryText,
        modifier = modifier.clickable { if (onClick != null) onClick() }
    )
}