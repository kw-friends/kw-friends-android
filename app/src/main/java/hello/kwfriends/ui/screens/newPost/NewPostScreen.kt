package hello.kwfriends.ui.screens.newPost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import hello.kwfriends.ui.component.FullTextField
import hello.kwfriends.ui.component.SingleTextField
import hello.kwfriends.ui.component.TagChip
import kotlinx.coroutines.launch


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewPostScreen(
    newPostViewModel: NewPostViewModel,
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackbarMessage by newPostViewModel.snackbarEvent.collectAsState()

    snackbarMessage?.let { message ->
        snackbarHostState.currentSnackbarData?.dismiss() // running snackbar 종료
        scope.launch {
            snackbarHostState.showSnackbar(message) // snackbar 표시
            newPostViewModel._snackbarEvent.value = null // _snackbarEvent 초기화
        }
    }
    SnackbarHost(snackbarHostState) { data ->
        // custom snackbar with the custom border
        Snackbar(
            actionOnNewLine = true,
            snackbarData = data
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        //top start
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material.IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "back button"
                )
            }
            Text(
                text = "새 모임 생성",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Default
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 40.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.size(10.dp))
            FullTextField(
                modifier = Modifier.padding(horizontal = 14.dp),
                placeholder = "",
                value = newPostViewModel.gatheringPromoter,
                canValueChange = false,
                onValueChange = {},
                externalTitle = "모임 주최자"
            )
            FullTextField(
                modifier = Modifier.padding(horizontal = 14.dp),
                placeholder = "",
                value = newPostViewModel.gatheringTitle,
                onValueChange = { newPostViewModel.gatheringTitleChange(it) },
                imeAction = ImeAction.Next,
                isError = !newPostViewModel.gatheringTitleStatus,
                externalTitle = "모임 제목",
                errorMessage = "필수 항목",
            )
            FullTextField(
                modifier = Modifier.padding(horizontal = 14.dp),
                placeholder = "",
                value = newPostViewModel.gatheringDescription,
                onValueChange = { newPostViewModel.gatheringDescriptionChange(it) },
                isSingleLine = false,
                maxLines = 6,
                imeAction = ImeAction.Default,
                isError = !newPostViewModel.gatheringDescriptionStatus,
                externalTitle = "모임 설명",
                errorMessage = "필수 항목",
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .height(intrinsicSize = IntrinsicSize.Min)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "최대 인원",
                    color = Color(0xFF636363),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                if (!newPostViewModel.participantsRangeValidation) {
                    Text(
                        text = "2명 이상, 100명 이하의 인원 수를 입력해 주세요.",
                        color = Color(0xFFFF0000),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(end = 14.dp)
                    )
                }
            }
            SingleTextField(
                modifier = Modifier.padding(horizontal = 14.dp),
                value = newPostViewModel.maximumParticipants,
                onValueChange = { newPostViewModel.maximumParticipantsChange(max = it) },
                imeAction = ImeAction.Done
            )
            Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                Text(
                    text = "모임 태그",
                    color = Color(0xFF636363),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            FlowRow(modifier = Modifier.padding(start = 14.dp)) {
                newPostViewModel.tagMap.forEach {
                    TagChip(
                        modifier = Modifier.padding(end = 4.dp),
                        text = it.key,
                        icon = Icons.Filled.Person,
                        selected = it.value,
                        onClick = { newPostViewModel.tagMap[it.key] = !it.value }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "커뮤니티 가이드라인을 준수하여 모임을 생성해 주세요!",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xff888888)
                )
                Button(
                    onClick = {
                        if (!newPostViewModel.isUploading) {
                            if (!newPostViewModel.validateGatheringInfo()) {
                                newPostViewModel.showSnackbar("모임 정보가 부족합니다.")
                            } else {
                                newPostViewModel.uploadGatheringToFirestore(onDismiss)
                            }
                        }
                    },
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .align(Alignment.End)
                ) {
                    if (!newPostViewModel.isUploading) {
                        Text(text = "모임 만들기", style = MaterialTheme.typography.labelLarge)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color(0xFF833538),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.size(7.dp))
                            Text(text = "업로드 중..", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}