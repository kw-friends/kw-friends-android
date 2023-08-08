package hello.kwfriends.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsItem(
    title: String,
    checked: Boolean,
    description: String = "",
    firstItem: Boolean = false
) {
    if (!firstItem) {
        Divider(
            color = Color.DarkGray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
        )
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Column(Modifier.weight(10F)) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight(650))
            if (description != "") {
                Text(text = description, fontSize = 15.sp)
            }
        }
        Switch(checked = checked, onCheckedChange = {/*TODO*/ }, Modifier.weight(2F))
    }
}

@Composable
fun SettingsScreen() {
    Column {
        SettingsItem(title = "다크 모드", checked = true, firstItem = true)
        SettingsItem(title = "조용 모드", checked = false, description = "모든 알림을 꺼 다른 일에 집중할 수 있어요")
        SettingsItem(
            title = "라면에 식초 한숟갈?",
            checked = true,
            description = "어승경만 아는 라면 레시피, 절대 실패할 일 없어요. 진짜에요!"
        )
    }
}