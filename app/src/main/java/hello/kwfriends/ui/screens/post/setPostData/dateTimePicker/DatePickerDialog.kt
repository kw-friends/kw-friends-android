package hello.kwfriends.ui.screens.post.setPostData.dateTimePicker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hello.kwfriends.ui.screens.post.setPostData.SetPostDataViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerPopup(
    onDismiss: () -> Unit,
    setPostDataViewModel: SetPostDataViewModel
) {
    val datePickerState = remember {
        DatePickerState(
            initialDisplayedMonthMillis = null,
            yearRange = (2023..2040), // KW Friends 서비스 종료 시점으로 설정
            initialDisplayMode = DisplayMode.Picker,
            initialSelectedDateMillis = setPostDataViewModel.gatheringDate
        )
    }

    DatePickerDialog(
        shape = RoundedCornerShape(24.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                setPostDataViewModel.onDateChanged(datePickerState.selectedDateMillis!!)
                onDismiss()
            }
            ) {
                Text(text = "확인", fontFamily = FontFamily.Default)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "취소", fontFamily = FontFamily.Default)
            }
        }
    ) {
        Box(modifier = Modifier) {
            DatePicker(
                title = { Text(text = "모임 날짜를 정해주세요") },
                headline = {
                    Text(
                        text = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(
                            setPostDataViewModel.gatheringDate
                        )
                    )
                },
                state = datePickerState,
                dateValidator = { timestamp ->
                    timestamp > Instant.now().toEpochMilli()
                },
                dateFormatter = DatePickerFormatter(yearSelectionSkeleton = "yyyy/MM/dd"),
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
fun DatePickerPopupPreview() {
    DatePickerPopup(
        onDismiss = {},
        setPostDataViewModel = SetPostDataViewModel()
    )
}