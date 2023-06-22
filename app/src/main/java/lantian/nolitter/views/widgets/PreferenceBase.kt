package lantian.nolitter.views.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
    text: String,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
) {
    PreferenceListItem(
        text = { Text(text) },
        icon = icon,
        trailing = trailing,
        modifier = modifier,
        secondaryText = secondaryText?.let {
            {
                Text(
                    text = secondaryText,
                    modifier = Modifier.alpha(0.67f)
                )
            }
        }
    )
}

@Composable
fun PreferenceListItem(
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val typography = MaterialTheme.typography
    val styledText: @Composable () -> Unit = {
        ProvideTextStyle(typography.bodyLarge, text)
    }
    val styledTrailing: @Composable (() -> Unit)? = trailing?.let {
        { ProvideTextStyle(typography.bodyMedium, trailing) }
    }
    val styledSecondaryText: @Composable (() -> Unit)? = secondaryText?.let {
        { ProvideTextStyle(typography.bodyMedium, secondaryText) }
    }
    Row(
        modifier
            .semantics(mergeDescendants = true) {}
            .heightIn(min = secondaryText?.let { 80.dp } ?: 64.dp)
    ) {
        if (icon != null) {
            Box(
                Modifier
                    .align(Alignment.CenterVertically)
                    .widthIn(min = 56.dp)
                    .padding(start = 16.dp)
            ) { icon() }
        }
        if (styledSecondaryText == null) {
            Box(
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(16.dp)
            ) { styledText() }
        } else {
            Column(
                Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .padding(16.dp)
            ) {
                text()
                styledSecondaryText()
            }
        }
        if (styledTrailing != null) {
            Box(
                Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp)
            ) { styledTrailing() }
        }
    }
}