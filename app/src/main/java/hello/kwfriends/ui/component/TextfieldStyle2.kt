package hello.kwfriends.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextfieldStyle2(text: String, value: String,  onValueChange: (String) -> Unit){
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .width(266.dp)
            .height(40.dp),
        interactionSource = interactionSource,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight(300),
            color = Color(0xFFF1F1F1),
            textAlign = TextAlign.Center
        ),

        ) { innerTextField ->
        val containerColor = Color(0x66F1F1F1)
        TextFieldDefaults.DecorationBox(
            value = "test",
            innerTextField = innerTextField,
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            shape = RoundedCornerShape(size = 10.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color(0xFFF1F1F1),
                unfocusedTextColor = Color(0xFFF1F1F1),
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                disabledContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            placeholder = {
                Text(text = text, color = Color(0xFFF1F1F1), fontWeight = FontWeight(300))
            },
            contentPadding = PaddingValues(0.dp), // this is how you can remove the padding
        )
    }
}

@Preview
@Composable
fun TextfieldStyle2Preview(){
    TextfieldStyle2(text = "Preview", value = "", onValueChange = {})
}