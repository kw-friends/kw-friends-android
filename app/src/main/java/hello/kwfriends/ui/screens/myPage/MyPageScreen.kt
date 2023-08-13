package hello.kwfriends.ui.screens.myPage

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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserInfoCard(
    userName: String,
    admissionyear: Int,
    major: String,
    grade: Int
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
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFF0D1D8),
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

@Preview
@Composable
fun UserInfoCardPreview() {
    UserInfoCard(userName = "어승경", admissionyear = 23, major = "소프트웨어학부", grade = 1)
}