package hello.kwfriends.ui.screens.settings.notice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import hello.kwfriends.firebase.realtimeDatabase.NoticeDetail
import hello.kwfriends.ui.component.AnnotatedClickableText
import hello.kwfriends.ui.screens.settings.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NoticeList(
    noticeDetail: NoticeDetail
) {
    var noticeExpended by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { noticeExpended = !noticeExpended }
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color(0xFFFAF3F3)),
            headlineContent = {
                Column {
                    Text(
                        text = noticeDetail.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight(500),
                        overflow = TextOverflow.Ellipsis
                    )
                    AnimatedVisibility(visible = noticeExpended) {
                        AnnotatedClickableText(
                            text = noticeDetail.content,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = SimpleDateFormat(
                            "yyyy/MM/dd", Locale.getDefault()
                        ).format(noticeDetail.uploadedTime),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight(500),
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeScreen(
    settingsViewModel: SettingsViewModel,
    notices: List<NoticeDetail>
) {
    LaunchedEffect(key1 = Unit) {
        settingsViewModel.initNoitces()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBFF)),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "공지사항",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W600
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { settingsViewModel.noticePopupState = false },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "back button",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
            )
        }
    ) { it ->
        Column(modifier = Modifier.padding(it)) {
            LazyColumn() {
                items(notices) { noticeData ->
                    if (notices.isNotEmpty()) {
                        NoticeList(noticeData)
                    } else {
                        Text(
                            text = "공지사항이 없습니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

    }
}