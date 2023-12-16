package hello.kwfriends.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsButtonItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    description: String = "",
    isUpperLine: Boolean = true
) {
    if(isUpperLine) {
        Divider(
            color = Color.LightGray,
            thickness = 0.5.dp,
        )
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(15.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 15.dp)
        ) {
            Column(Modifier.weight(10F)) {
                Box{
                    Text(text = title, style = MaterialTheme.typography.bodyLarge, fontFamily = FontFamily.Default)
                    if (description != "") {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Default,
                            color = Color.DarkGray,
                            modifier = Modifier
                                .padding(top = 23.dp)
                                .widthIn(max = 220.dp)
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
        }
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun SettingsButtonItemPreview() {
    SettingsButtonItem(
        title = "Preview",
        onClick = {},
        description = "This is SettingsButtonItem component preview."
    )
}