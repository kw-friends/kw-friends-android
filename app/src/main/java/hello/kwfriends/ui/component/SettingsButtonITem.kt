package hello.kwfriends.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp

@Composable
fun SettingsButtonItem(
    title: String,
    description: String = "",
    onClick: () -> Unit
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
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clickable { onClick() }
            .clip(shape = AbsoluteRoundedCornerShape(10.dp))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(10F)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                if (description != "") {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                    )
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