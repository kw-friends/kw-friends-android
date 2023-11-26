package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTags(tagList: List<String>, removeTag: (String) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() } //클릭 시각적 효과 제거하기 위함
    FlowRow {
        for(item in tagList) {
            Row(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .height(35.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE9E9E9)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 10.dp, end = 2.dp),
                )
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "태그 제거",
                    tint = Color.DarkGray,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 10.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { removeTag(item) }
                        )
                )
            }
        }
        Box(
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape)
                .background(Color(0xFFE9E9E9)),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "태그 추가",
                tint = Color.DarkGray,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}