package hello.kwfriends.ui.screens.settings


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hello.kwfriends.ui.component.UserInfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigation: NavController
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "설정",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(5.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE2A39B)
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navigation.popBackStack() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go to HomeScreen",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(it)
        ) {
            UserInfoCard(
                userName = "!23",
                admissionYear = 23,
                major = "소프트웨어학부",
                grade = 1,
                navigation = navigation
            )
            SettingsSwitchItem(title = "다크 모드", checked = true)
            SettingsSwitchItem(
                title = "조용 모드",
                checked = false,
                description = "모든 알림을 꺼 다른 일에 집중할 수 있어요"
            )
            SettingsSwitchItem(
                title = "라면에 식초 한숟갈?",
                checked = true,
                description = "어승경만 아는 라면 레시피, 절대 실패할 일 없어요. 진짜에요!"
            )
            SettingsButtonItem(
                title = "로그아웃",
                onClick = {}
            )
            SettingsButtonItem(
                title = "회원탈퇴",
                onClick = {}
            )

        }
    }
}


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
