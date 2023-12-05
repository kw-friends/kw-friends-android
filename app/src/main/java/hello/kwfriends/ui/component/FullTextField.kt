package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hello.kwfriends.ui.theme.AppFont

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FullTextField(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    canValueChange: Boolean = true,
    value: String,
    onValueChange: (String) -> Unit,
    maxLines: Int = 1,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    externalTitle: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val interactionSource = remember { MutableInteractionSource() }
    var containerColor by remember { mutableStateOf(Color(0x66F1F1F1)) }
    var textColor by remember { mutableStateOf(Color(0xFF000000)) }
    var placeholderColor by remember { mutableStateOf(Color(0xFFF1F1F1)) }

    if (externalTitle != "" || isError) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .height(intrinsicSize = IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            if (externalTitle != "") {
                Text(
                    text = externalTitle,
                    color = Color(0xFF636363),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
            }
            if (isError) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF0000),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(end = 14.dp)
                )
            }
        }
    }

    BasicTextField(value = value,
        onValueChange = if (canValueChange) onValueChange else { _ -> Unit },
        enabled = canValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(
                Modifier
                    .height(IntrinsicSize.Min)
                    .heightIn(min = 48.dp)
            )
            .onFocusChanged {
                if (it.isFocused && canValueChange) {
                    containerColor = Color(0xFFDADADA)
                    textColor = Color.Black
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
            },
        interactionSource = interactionSource,
        singleLine = isSingleLine,
        textStyle = TextStyle(
            fontFamily = AppFont.defaultFontFamily,
            color = textColor,
            textAlign = TextAlign.Start,
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) {
            KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction)
        } else {
            KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction)
        },
        cursorBrush = SolidColor(Color(0xF1363636)),
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
                    .clip(RoundedCornerShape(10.dp))
                    .background(containerColor)
                    .padding(horizontal = 12.dp),
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
fun TextfieldStyle3Preview() {
    FullTextField(placeholder = "Preview", value = "", onValueChange = {})
}