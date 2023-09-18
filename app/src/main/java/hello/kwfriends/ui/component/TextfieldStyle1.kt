package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextfieldStyle1(placeholder: String, icon: ImageVector, isPassword: Boolean = false, value: String,  onValueChange: (String) -> Unit){
    val interactionSource = remember { MutableInteractionSource() }
    var containerColor by remember { mutableStateOf(Color(0x66F1F1F1)) }
    var textColor by remember { mutableStateOf(Color(0xFFF1F1F1)) }
    var placeholderColor by remember { mutableStateOf(Color(0xFFF1F1F1)) }

    Box {
        Row {
            Spacer(
                modifier = Modifier
                    .width(56.dp)
                    .height(51.dp)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .width(208.dp)
                    .height(51.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            containerColor = Color(0xFFF1F1F1)
                            textColor = Color.Black
                            placeholderColor = Color.Gray
                        } else {
                            containerColor = Color(0x66F1F1F1)
                            textColor = Color(0xFFF1F1F1)
                            placeholderColor = Color(0xFFF1F1F1)
                        }
                    }
                ,
                interactionSource = interactionSource,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(300),
                    color = textColor,
                    textAlign = TextAlign.Center
                ),
                visualTransformation = if(isPassword && value.isNotEmpty()) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = if(isPassword && value.isNotEmpty()) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(0.dp, 10.dp, 10.dp, 0.dp))
                            .background(containerColor),
                        contentAlignment = Alignment.Center
                    ){
                        if(value.isEmpty())
                            Text(
                                text = placeholder,
                                fontSize = 20.sp,
                                fontWeight = FontWeight(300),
                                color = placeholderColor,
                                textAlign = TextAlign.Center
                            )
                        // you have to invoke this function then cursor will focus and you will able to write something
                        innerTextField.invoke()
                    }
                }
            )
        }
        Box {
            Spacer(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp, 0.dp, 0.dp, 10.dp))
                    .background(Color(0xFFDB8B8B))
                    .width(58.dp)
                    .height(51.dp)
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0x66F1F1F1),
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center)
            )
        }


    }
}

@Preview
@Composable
fun TextfieldStyle1Preview() {
    TextfieldStyle1("Preview", Icons.Default.Preview, false,  "") {}
}