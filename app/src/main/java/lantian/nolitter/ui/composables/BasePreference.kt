package lantian.nolitter.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun BasePreference(
    text: String, secondaryText: String? = null, modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null, icon: (@Composable () -> Unit)? = null
) {
    ListItem(
        text = { Text(text) }, icon = icon, trailing = trailing,
        secondaryText = if (secondaryText != null) {{ Text(secondaryText) }} else null,
        modifier = modifier.padding(vertical = 8.dp)
    )
}