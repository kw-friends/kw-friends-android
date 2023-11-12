package hello.kwfriends.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsButtonItem(
    title: String,
    onClick: () -> Unit,
    description: String = "",
) {
    Divider(
        color = Color(0xFF353535),
        thickness = 0.5.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() }
            .clip(shape = AbsoluteRoundedCornerShape(10.dp))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(10F)) {
                Box{
                    Text(text = title, style = MaterialTheme.typography.titleMedium,)
                    if (description != "") {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                            modifier = Modifier
                                .padding(top = 30.dp)
                                .widthIn(max = 220.dp)
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
                tint = Color.DarkGray
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