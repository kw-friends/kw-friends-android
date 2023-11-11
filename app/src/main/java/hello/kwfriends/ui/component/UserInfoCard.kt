package hello.kwfriends.ui.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@Composable
fun UserInfoCard(
    userName: String,
    admissionYear: String,
    major: String,
    navigation: NavController
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .clip(shape = RoundedCornerShape(18.dp))
            .background(Color(0xFFF3ADBD))

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight(700),
                modifier = Modifier.padding(start = 12.dp)
            )
            Button(
                onClick = { navigation.navigate(Routes.AUTH_SCREEN) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF0D1D8),
                    contentColor = Color(0xFF111111)
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(text = "내 정보 수정", style = MaterialTheme.typography.labelLarge)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFFEBDACF))
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .height(IntrinsicSize.Min)
        ) {
            Text(text = major)
            Divider(
                color = Color.DarkGray,
                modifier = Modifier
                    .height(18.dp)
                    .padding(horizontal = 6.dp)
                    .width(1.dp)
            )
            Text(text = "${admissionYear}학번")
        }
    }
}