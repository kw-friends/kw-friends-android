package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTags() {
    FlowRow {
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