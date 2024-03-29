package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CheckboxStyle2(
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 0.dp,
    text: String,
    textColor: Color,
    checkBoxSize: Dp = 16.dp,
    checked: Boolean,
    onClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClicked() }
            .padding(vertical = verticalPadding)
    ) {
        Box {
            Spacer(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .size(checkBoxSize)
                    .background(Color(0xFFF1F1F1))
            )
            Checkbox(
                checked = checked,
                onCheckedChange = { onClicked() },
                colors = CheckboxDefaults.colors(
                    uncheckedColor = Color.Transparent,
                    checkedColor = Color.Transparent,
                    checkmarkColor = Color.Red
                ),
                modifier = Modifier
                    .size(checkBoxSize)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
    }

}

@Preview
@Composable
fun CheckboxStyle2Preview() {
    CheckboxStyle1("Preview", Color(0xFFF1F1F1), true, {}, {})
}