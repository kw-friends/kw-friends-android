package hello.kwfriends.ui.screens.chattingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hello.kwfriends.ui.main.Routes

@Composable
fun ChattingListScreen(
    chattingLIstViewModel: ChattingLIstViewModel,
    navigation: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBFF))
    ) {
        //top start
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navigation.navigate(Routes.HOME_SCREEN) }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "back button"
                )
            }
            Text(
                text = "참가중인 채팅 목록",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Default
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            //top
            Box(
                modifier = Modifier
                    .padding(top = 60.dp)
                    .fillMaxWidth()
            ) {
                Box(modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.align(Alignment.TopStart)) {
                        Text(
                            text = "테스트용 채팅방",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight(600)
                        )
                        Text(
                            text = "새해 복 많이받으세요~!",
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily.Default,
                        )
                    }
                    Text(
                        modifier = Modifier.align(Alignment.TopEnd),
                        text = "2023/12/31",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Default,
                        color = Color.Gray
                    )
                }
            }
            Divider(
                modifier = Modifier.padding(horizontal = 5.dp),
                color = Color.LightGray,
                thickness = 0.5.dp,
            )
        }
    }
}