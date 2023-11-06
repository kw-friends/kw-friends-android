package hello.kwfriends.ui.screens.settings


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hello.kwfriends.ui.screens.main.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navigation: NavController) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "설정",
                        fontSize = 22.sp,
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
                userName = "어승경",
                admissionyear = 23,
                major = "소프트웨어학부",
                grade = 1,
                navigation = navigation
            )
            Spacer(modifier = Modifier.height(7.dp))
            SettingsSwitchItem(title = "다크 모드", checked = true, firstItem = true)
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
    firstItem: Boolean = false,
    onClick: () -> Unit
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
            .clickable { onClick() }
    ) {
        Column(Modifier.weight(10F)) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight(550))
            if (description != "") {
                Text(
                    text = description,
                    fontSize = 15.sp,
                )
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
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
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight(550))
            if (description != "") {
                Text(text = description, fontSize = 15.sp)
            }
        }
        Switch(checked = checked, onCheckedChange = {/*TODO*/ }, Modifier.weight(2F))
    }
}


@Composable
fun UserInfoCard(
    userName: String,
    admissionyear: Int,
    major: String,
    grade: Int,
    navigation: NavController
) {
    Column(
        modifier = Modifier
            .padding(15.dp)
            .clip(shape = RoundedCornerShape(15.dp))
            .background(Color(0xFFF3ADBD))

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 17.dp)
                .height(IntrinsicSize.Min)
        ) {
            Text(
                text = userName,
                fontSize = 35.sp,
                fontWeight = FontWeight(600),
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 15.dp)
            )
            Button(
                onClick = { navigation.navigate(Routes.AUTH_SCREEN) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF0D1D8),
                    contentColor = Color(0xFF111111)
                ),
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(text = "내 정보 수정", fontSize = 15.sp)
            }
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                .background(Color(0xFFEBDACF))
                .fillMaxWidth()
                .padding(vertical = 15.dp, horizontal = 25.dp)
                .height(IntrinsicSize.Min)
        ) {
            Text(text = major)
            Divider(
                color = Color.DarkGray,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 6.dp)
                    .width(1.dp)
            )
            Text(text = "${admissionyear}학번")
            Divider(
                color = Color.DarkGray,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 6.dp)
                    .width(1.dp)
            )
            Text(text = "${grade}학년")
        }
    }
}