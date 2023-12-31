package hello.kwfriends.ui.screens.post.setPostData.dateTimePicker

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimePickerComponent(
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val interactionSource = remember { MutableInteractionSource() }
    var textColor by remember { mutableStateOf(Color(0xFF000000)) }
    var containerColor by remember { mutableStateOf(Color(0xFFEBEBEB)) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .width(60.dp)
            .wrapContentHeight()
            .onFocusChanged {
                if (it.isFocused) {
                    textColor = Color.Black
                    containerColor = Color(0xFFF7F7F7)
                } else {
                    textColor = Color(0xFFA83544)
                    containerColor = Color(0xFFEBEBEB)
                }
            },
        interactionSource = interactionSource,
        singleLine = true,
        maxLines = 1,
        textStyle = TextStyle(
            fontFamily = FontFamily.Default,
            color = textColor,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            keyboardController?.hide()
        }),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(containerColor)
                    .width(IntrinsicSize.Min)
                    .widthIn(min = 45.dp)
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp, vertical = 10.dp) //offset?
            ) {
                innerTextField()
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimePickerStyle(
    modifier: Modifier = Modifier,
    hourValue: String,
    minuteValue: String,
    onHourValueChange: (String) -> Unit,
    onMinuteValueChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimePickerComponent(
            value = hourValue,
            onValueChange = onHourValueChange,
            imeAction = ImeAction.Next
        )
        Text(
            text = ":",
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.W900,
            modifier = Modifier.padding(horizontal = 2.dp),
            color = Color(0xFFA83544)
        )
        TimePickerComponent(
            value = minuteValue,
            onValueChange = onMinuteValueChange,
            imeAction = ImeAction.Done
        )
    }

}

@Preview
@Composable
fun TimePickerStylePreview() {
    TimePickerStyle(
        hourValue = "10",
        minuteValue = "11",
        onHourValueChange = {},
        onMinuteValueChange = {}
    )
}