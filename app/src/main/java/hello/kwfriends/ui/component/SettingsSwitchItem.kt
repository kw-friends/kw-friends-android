package hello.kwfriends.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsSwitchItem(
    title: String,
    checked: Boolean,
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
            .padding(horizontal = 16.dp)
            .heightIn(min = 70.dp)
    ) {
        Column() {
            Text(text = title, style = MaterialTheme.typography.titleMedium, lineHeight = 1.sp)
            if (description != "") {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.widthIn(max = 220.dp)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = {/*TODO*/ },
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}