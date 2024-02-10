package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChattingTextField(
    placeholder: String = "",
    canValueChange: Boolean = true,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    bottomPadding: Dp = 0.dp,
    imageSelect: () -> Unit,
    chattingSend: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val interactionSource = remember { MutableInteractionSource() }
    var containerColor by remember { mutableStateOf(Color(0x66F1F1F1)) }
    var textColor by remember { mutableStateOf(Color(0xFF000000)) }
    var placeholderColor by remember { mutableStateOf(Color(0xFFF1F1F1)) }

    BasicTextField(value = value,
        onValueChange = if (canValueChange) onValueChange else { _ -> Unit },
        enabled = canValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(
                Modifier
                    .height(IntrinsicSize.Min)
                    .heightIn(min = 40.dp)
            )
            .onFocusChanged {
                if (it.isFocused && canValueChange) {
                    containerColor = Color(0xFFE9E9E9)
                    textColor = Color(0xFF161616)
                    placeholderColor = Color(0xFF4B4B4B)
                } else if (!it.isFocused && canValueChange) {
                    containerColor = Color(0xFFE9E9E9)
                    textColor = Color(0xFF161616)
                    placeholderColor = Color(0xFF4B4B4B)
                } else {
                    containerColor = Color(0xFFC7C4C4)
                    textColor = Color(0xFF4B4B4B)
                    placeholderColor = Color(0xFF4B4B4B)
                }
            }
            .padding(bottom = bottomPadding),
        interactionSource = interactionSource,
        textStyle = MaterialTheme.typography.bodyLarge,
        visualTransformation = VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        cursorBrush = SolidColor(Color(0xF1161616)),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            keyboardController?.hide()
        }),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .background(Color(0xFFE9E9E9)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .clickable { imageSelect() }
                        .fillMaxHeight()
                        .padding(10.dp)
                        .size(25.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "add button"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .weight(1f),
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
                Icon(
                    modifier = Modifier
                        .clickable { chattingSend() }
                        .fillMaxHeight()
                        .padding(10.dp)
                        .size(24.dp),
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "add button"
                )
            }
        }
    )
}

@Preview
@Composable
fun ChattingTextFieldPreview() {
    FullTextField(placeholder = "Preview", value = "", onValueChange = {})
}