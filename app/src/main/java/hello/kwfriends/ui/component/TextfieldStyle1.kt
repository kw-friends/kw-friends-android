package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextfieldStyle1(text: String, icon: ImageVector, value: String, isPassword: Boolean = false, onValueChange: (String) -> Unit){
    Box {
        Row {
            Spacer(
                modifier = Modifier
                    .width(56.dp)
                    .height(51.dp)
            )
            val containerColor = Color(0x66F1F1F1)
            TextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                placeholder = {
                    Text(text = text, color = Color(0xFFF1F1F1), fontWeight = FontWeight(300))
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(0xFFF1F1F1),
                    unfocusedTextColor = Color(0xFFF1F1F1),
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                textStyle = TextStyle(
                    fontWeight = FontWeight(300),
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(0.dp, 10.dp, 10.dp, 0.dp))
                    .width(208.dp)
                    .height(51.dp),
                visualTransformation = if(isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = if(isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default
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
    TextfieldStyle1("Preview", Icons.Default.Preview, "",  false) {}
}