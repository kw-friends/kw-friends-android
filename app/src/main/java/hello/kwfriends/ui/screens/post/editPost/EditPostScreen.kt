package hello.kwfriends.ui.screens.post.editPost

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.ui.component.FullTextField
import hello.kwfriends.ui.component.SingleTextField
import hello.kwfriends.ui.component.TagChip
import hello.kwfriends.ui.component.TimePickerStyle
import hello.kwfriends.ui.screens.post.editPost.dateTimePicker.DatePickerPopup
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditPostScreen(
    editPostViewModel: EditPostViewModel,
    postDetail: PostDetail,
    onDismiss: () -> Unit
) {
    DatePickerPopup(
        state = editPostViewModel.datePickerPopupState,
        onDismiss = { editPostViewModel.datePickerPopupState = false },
        editPostViewModel = editPostViewModel
    )

    LaunchedEffect(true) {
        editPostViewModel.initPostData(postDetail)
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFBFF))
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "back button"
                )
            }
            Text(
                text = "내용 수정∙편집하기",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Default
            )
        }

        Column(
            modifier = Modifier
                .padding(
                    top = 40.dp,
                    start = 14.dp,
                    end = 14.dp
                ) // (vertical = 40.dp, horizontal = 14.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.size(10.dp))
            FullTextField(
                placeholder = "",
                value = editPostViewModel.gatheringPromoter,
                canValueChange = false,
                onValueChange = {},
                externalTitle = "모임 주최자"
            )
            FullTextField(
                placeholder = "",
                value = editPostViewModel.gatheringTitle,
                onValueChange = { editPostViewModel.gatheringTitleChange(it) },
                imeAction = ImeAction.Next,
                isError = !editPostViewModel.gatheringTitleStatus,
                externalTitle = "모임 제목",
                errorMessage = "필수 항목",
            )
            FullTextField(
                placeholder = "",
                value = editPostViewModel.gatheringDescription,
                onValueChange = { editPostViewModel.gatheringDescriptionChange(it) },
                isSingleLine = false,
                maxLines = 6,
                imeAction = ImeAction.Default,
                isError = !editPostViewModel.gatheringDescriptionStatus,
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
                )
                if (!editPostViewModel.participantsRangeValidation) {
                    Text(
                        text = "2명 이상, 100명 이하의 인원 수를 입력해 주세요.",
                        color = Color(0xFFFF0000),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            SingleTextField(
                value = editPostViewModel.maximumParticipants,
                onValueChange = { editPostViewModel.maximumParticipantsChange(max = it) },
                imeAction = ImeAction.Done
            )
            Text(
                text = "모임 일시",
                color = Color(0xFF636363),
                style = MaterialTheme.typography.labelMedium,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    editPostViewModel.datePickerPopupState = true
                }) {
                    Text(
                        text = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(
                            editPostViewModel.date
                        ),
                    )
                }
                TimePickerStyle(
                    hourValue = editPostViewModel.gatheringHour,
                    minuteValue = editPostViewModel.gatheringMinute,
                    onHourValueChange = { editPostViewModel.onHourChanged(it) },
                    onMinuteValueChange = { editPostViewModel.onMinuteChanged(it) }
                )
            }
            Text(
                text = "모임 장소",
                color = Color(0xFF636363),
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = "모임 태그",
                color = Color(0xFF636363),
                style = MaterialTheme.typography.labelMedium,
            )
            FlowRow {
                editPostViewModel.tagMap.forEach {
                    TagChip(
                        modifier = Modifier.padding(end = 4.dp),
                        text = it.key,
                        selected = it.value,
                        onClick = { editPostViewModel.updateTagMap(it.key) }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "커뮤니티 가이드라인을 준수하여 모임을 생성해 주세요!",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xff888888)
                )
                Button(
                    onClick = {
                        if (!editPostViewModel.isUploading) {
                            if (!editPostViewModel.validateGatheringInfo()) {
                                editPostViewModel.showSnackbar("모임 정보가 부족합니다.")
                            } else {
                                editPostViewModel.updatePostInfoToFirestore(onDismiss)
                            }
                        }
                    },
                    enabled = !editPostViewModel.isUploading,
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .align(Alignment.End)
                ) {
                    if (!editPostViewModel.isUploading) {
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