package hello.kwfriends.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ButtonStyle1(text: String, onClick: () -> Unit){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFBD3434),
            contentColor = Color(0xFFF1F1F1)
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .width(266.dp)
            .height(51.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFFF1F1F1),
            )
        )
    }
}

@Preview
@Composable
fun ButtonStyle1Preview2() {
    ButtonStyle1(text = "Preview") {}
}