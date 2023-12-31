package hello.kwfriends.ui.screens.post.setPostData

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import hello.kwfriends.firebase.realtimeDatabase.Action
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.ui.component.FullTextField
import hello.kwfriends.ui.component.SingleTextField
import hello.kwfriends.ui.component.TagChip
import hello.kwfriends.ui.screens.home.HomeViewModel
import hello.kwfriends.ui.screens.post.setPostData.dateTimePicker.DatePickerPopup
import hello.kwfriends.ui.screens.post.setPostData.dateTimePicker.TimePickerStyle
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalAnimationApi::class)
@Composable
fun SetPostDataScreen(
    setPostDataViewModel: SetPostDataViewModel,
    homeViewModel: HomeViewModel,
    postDetail: PostDetail?,
    onDismiss: () -> Unit,
    state: Action
) {
    DatePickerPopup(
        state = setPostDataViewModel.datePickerPopupState,
        onDismiss = { setPostDataViewModel.datePickerPopupState = false },
        setPostDataViewModel = setPostDataViewModel
    )

    LaunchedEffect(true) {
        setPostDataViewModel.initPostData(postDetail = postDetail, state = state)
    }

    val scrollState = rememberScrollState()
    var additionalInfoVisibility by remember { mutableStateOf(false) }

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
                text = if (state == Action.MODIFY) "내용 수정∙편집" else "새 모임 생성",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Default
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 40.dp, start = 14.dp, end = 14.dp, bottom = 4.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "기본 정보 설정",
                style = MaterialTheme.typography.titleSmall
            )
            FullTextField(
                placeholder = "",
                value = setPostDataViewModel.gatheringPromoter,
                canValueChange = false,
                onValueChange = {},
                externalTitle = "모임 주최자"
            )
            FullTextField(
                placeholder = "",
                value = setPostDataViewModel.gatheringTitle,
                onValueChange = { setPostDataViewModel.gatheringTitleChange(it) },
                imeAction = ImeAction.Next,
                isError = !setPostDataViewModel.gatheringTitleStatus,
                externalTitle = "모임 제목",
                errorMessage = "필수 항목",
            )
            FullTextField(
                placeholder = "",
                value = setPostDataViewModel.gatheringDescription,
                onValueChange = { setPostDataViewModel.gatheringDescriptionChange(it) },
                isSingleLine = false,
                maxLines = 6,
                imeAction = ImeAction.Default,
                isError = !setPostDataViewModel.gatheringDescriptionStatus,
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
                if (!setPostDataViewModel.participantsRangeValidation) {
                    Text(
                        text = "2명 이상, 100명 이하의 인원 수를 입력해 주세요.",
                        color = Color(0xFFFF0000),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            SingleTextField(
                value = setPostDataViewModel.maximumParticipants,
                onValueChange = { setPostDataViewModel.maximumParticipantsChange(max = it) },
                imeAction = ImeAction.Done
            )
            Column(
                modifier = Modifier
                    .padding(top = 14.dp, end = 4.dp)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(Color(0xFFFCEEEE))
                    .clickable {
                        additionalInfoVisibility = !additionalInfoVisibility
                        setPostDataViewModel.gatheringTimeLocationUse =
                            !setPostDataViewModel.gatheringTimeLocationUse
                    }
            ) {
                AnimatedContent(
                    targetState = additionalInfoVisibility,
                    transitionSpec = {
                        if (additionalInfoVisibility) {
                            (slideInHorizontally { height -> height } + fadeIn()).togetherWith(
                                slideOutHorizontally { height -> -height } + fadeOut())
                        } else {
                            (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(
                                slideOutHorizontally { height -> height } + fadeOut())
                        }.using(
                            SizeTransform(clip = false)
                        )
                    }, label = "additionalInfoVisibility = $additionalInfoVisibility"
                ) { targetState ->
                    if (targetState) {
                        Text(
                            text = "좋아요! 시간∙위치 정보를 추가해 주세요.",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = W600,
                            modifier = Modifier.padding(horizontal = 7.dp)
                        )
                    } else {
                        Text(
                            text = "시간∙위치 정보를 추가할까요?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = W600,
                            modifier = Modifier.padding(horizontal = 7.dp)
                        )
                    }
                }

                AnimatedVisibility(additionalInfoVisibility) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 7.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "모임 일시",
                                color = Color(0xFF636363),
                                style = MaterialTheme.typography.labelMedium,
                            )
                            if (!setPostDataViewModel.gatheringTimeValidation) {
                                Text(
                                    text = setPostDataViewModel.gatheringTimeMessage,
                                    color = Color(0xFFFF0000),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                setPostDataViewModel.datePickerPopupState = true
                            }) {
                                Text(
                                    text = SimpleDateFormat(
                                        "yyyy/MM/dd",
                                        Locale.getDefault()
                                    ).format(
                                        setPostDataViewModel.date
                                    ),
                                )
                            }
                            TimePickerStyle(
                                hourValue = setPostDataViewModel.gatheringHour,
                                minuteValue = setPostDataViewModel.gatheringMinute,
                                onHourValueChange = { setPostDataViewModel.onHourChanged(it) },
                                onMinuteValueChange = { setPostDataViewModel.onMinuteChanged(it) }
                            )
                        }
                        Text(
                            text = "모임 장소",
                            color = Color(0xFF636363),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
            Text(
                text = "모임 태그",
                color = Color(0xFF636363),
                style = MaterialTheme.typography.labelMedium,
            )
            FlowRow {
                setPostDataViewModel.tagMap.forEach {
                    TagChip(
                        modifier = Modifier.padding(end = 4.dp),
                        text = it.key,
                        selected = it.value,
                        onClick = { setPostDataViewModel.updateTagMap(it.key) }
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
                        if (!setPostDataViewModel.isUploading) {
                            if (!setPostDataViewModel.validateGatheringInfo()) {
                                setPostDataViewModel.showSnackbar("모임 정보가 부족합니다.")
                            } else if (homeViewModel.setPostDataState.first == Action.ADD) {
                                setPostDataViewModel.uploadPostInfoToFirestore(onDismiss)
                            } else { // homeViewModel.setPostDataState.first == Action.MODIFY
                                setPostDataViewModel.updatePostInfoToFirestore(onDismiss)
                            }
                        }
                    },
                    enabled = !setPostDataViewModel.isUploading,
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .align(Alignment.End)
                ) {
                    if (!setPostDataViewModel.isUploading) {
                        Text(
                            text = if (state == Action.MODIFY) "모임 수정하기" else "모임 만들기",
                            style = MaterialTheme.typography.labelLarge
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color(0xFF833538),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.size(7.dp))
                            Text(
                                text = if (state == Action.MODIFY) "수정 중.." else "생성 중..",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}