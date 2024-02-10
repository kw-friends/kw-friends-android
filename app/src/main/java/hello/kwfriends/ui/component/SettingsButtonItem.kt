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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
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
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    description: String = "",
    isUpperLine: Boolean = true
) {
    if (isUpperLine) {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = Color.LightGray
        )
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Column(Modifier.weight(10F)) {
                Box {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (description != "") {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                            modifier = Modifier
                                .padding(top = 23.dp)
                                .widthIn(max = 220.dp)
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
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