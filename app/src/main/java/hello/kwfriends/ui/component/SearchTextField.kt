package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    value: String,
    onValueChange: (String) -> Unit,
    enable: Boolean = true,
    maxLines: Int = 1,
    isSingleLine: Boolean = true,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val interactionSource = remember { MutableInteractionSource() }
    var containerColor by remember { mutableStateOf(Color(0x66F1F1F1)) }
    var textColor by remember { mutableStateOf(Color(0xFF000000)) }
    var placeholderColor by remember { mutableStateOf(Color(0xFFF1F1F1)) }

    BasicTextField(
        value = value,
        onValueChange = if (enable) onValueChange else { _ -> Unit },
        enabled = enable,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(
                Modifier
                    .height(IntrinsicSize.Min)
                    .heightIn(min = 40.dp)
            )
            .onFocusChanged {
                if (it.isFocused) {
                    containerColor = Color(0xFFE9E9E9)
                    textColor = Color.Black
                    placeholderColor = Color(0xFF4B4B4B)
                } else {
                    containerColor = Color(0xFFE9E9E9)
                    textColor = Color(0xFF161616)
                    placeholderColor = Color(0xFF4B4B4B)
                }
            },
        interactionSource = interactionSource,
        singleLine = isSingleLine,
        textStyle = MaterialTheme.typography.bodyMedium,
        cursorBrush = SolidColor(Color(0xF1161616)),
        maxLines = maxLines,
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            keyboardController?.hide()
        }),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(30.dp))
                    .background(containerColor)
                    .padding(start = 13.dp, end = 50.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = style,
                        color = placeholderColor,
                        textAlign = TextAlign.Start,
                    )
                }
                innerTextField.invoke()
            }
        }
    )
}

@Preview
@Composable
fun BasicTextFieldPreview() {
    SearchTextField(placeholder = "Preview", value = "", onValueChange = {})
}