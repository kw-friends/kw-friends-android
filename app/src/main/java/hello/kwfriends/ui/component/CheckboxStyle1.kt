package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheckboxStyle1(
    text: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    onTextClicked: () -> Unit
) {
    Row {
        Box {
            Spacer(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .size(15.dp)
                    .background(Color(0xFFF1F1F1))
            )
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    uncheckedColor = Color.Transparent,
                    checkedColor = Color.Transparent,
                    checkmarkColor = Color(0xFF1F1F1F)
                ),
                modifier = Modifier
                    .width(15.dp)
                    .height(15.dp)
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            color = Color(0xFFF1F1F1),
            style = TextStyle(
                fontWeight = FontWeight(300),
                fontSize = 11.sp
            ),
            modifier = Modifier.clickable(onClick = onTextClicked)
        )
    }

}

@Preview
@Composable
fun CheckboxStyle1Preview() {
    CheckboxStyle1("Preview", true, {}, {})
}