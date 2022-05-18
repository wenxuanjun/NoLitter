package lantian.nolitter.interfaces.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceBase(
    text: String, modifier: Modifier = Modifier, secondaryText: String? = null,
    trailing: (@Composable () -> Unit)? = null, icon: (@Composable () -> Unit)? = null,
) {
    PreferenceListItem(
        text = { Text(text) }, icon = icon,
        trailing = trailing, modifier = modifier,
        secondaryText = if (secondaryText != null) {{ Text(secondaryText, Modifier.alpha(0.67f)) }} else null
    )
}

@Composable
fun PreferenceListItem(
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    val typography = MaterialTheme.typography
    val styledText: @Composable () -> Unit = { ProvideTextStyle(typography.bodyLarge, text) }
    val styledTrailing: @Composable (() -> Unit)? = if (trailing != null) {{ ProvideTextStyle(typography.bodyMedium, trailing) }} else null
    val styledSecondaryText: @Composable (() -> Unit)? = if (secondaryText != null) {{ ProvideTextStyle(typography.bodyMedium, secondaryText) }} else null
    Row(modifier.semantics(mergeDescendants = true) {}.heightIn(min = if (secondaryText != null) 80.dp else 64.dp)) {
        if (icon != null) { Box(modifier = Modifier.align(Alignment.CenterVertically).widthIn(min = 56.dp).padding(start = 16.dp)) { icon() } }
        if (styledSecondaryText == null) {Box(modifier = Modifier.weight(1f).align(Alignment.CenterVertically).padding(16.dp)) { styledText() } }
        else { Column(Modifier.align(Alignment.CenterVertically).weight(1f).padding(16.dp)) { text(); styledSecondaryText() } }
        if (styledTrailing != null) { Box(Modifier.align(Alignment.CenterVertically).padding(end = 16.dp)) { styledTrailing() } }
    }
}