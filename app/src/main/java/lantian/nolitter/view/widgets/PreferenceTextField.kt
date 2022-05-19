package lantian.nolitter.view.widgets

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import lantian.nolitter.R

@Composable
fun PreferenceEditText(
    text: String, modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null, secondaryText: String? = null,
    onSubmit: ((String) -> Unit)? = null, dialogTitle: String, dialogDefaultContent: String? = "",
) {
    var showDialog by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf(dialogDefaultContent ?: "") }
    PreferenceDialog(
        text = text, icon = icon, showDialog = showDialog,
        secondaryText = secondaryText, modifier = modifier,
        onShowDialogChange = { showDialog = it }, dialogTitle = dialogTitle,
        dialogContent = {
            EmptyTextField(
                value = textValue, singleLine = true,
                onValueChange = { textValue = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
            )
        },
        dialogActions = {
            TextButton(onClick = { showDialog = false; if (onSubmit != null) onSubmit(textValue) }) {
                Text(stringResource(R.string.ui_dialogAccept))
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EmptyTextField(
    value: String, onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier, enabled: Boolean = true,
    readOnly: Boolean = false, textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null, placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null, trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false, visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default, keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false, maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) {
    val textColor = textStyle.color.takeOrElse { colors.textColor(enabled).value }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor(isError).value),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        modifier = modifier
            .indicatorLine(enabled, isError, interactionSource, colors)
            .defaultMinSize(minHeight = 48.dp),
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = value, enabled = enabled,
                label = label, isError = isError,
                placeholder = placeholder, leadingIcon = leadingIcon,
                trailingIcon = trailingIcon, singleLine = singleLine,
                colors = colors, interactionSource = interactionSource,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField, contentPadding = PaddingValues(0.dp)
            )
        }
    )
}