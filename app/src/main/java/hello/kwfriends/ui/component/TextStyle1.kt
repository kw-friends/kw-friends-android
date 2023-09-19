package hello.kwfriends.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun TextStyle1(text: String){
    Text(
        text = text,
        style = TextStyle(
            fontSize = 25.sp,
            fontWeight = FontWeight(700),
            color = Color(0xFF73212C),
        )
    )
}

@Preview
@Composable
fun TextStylePreview(){
    TextStyle1("Preview")
}