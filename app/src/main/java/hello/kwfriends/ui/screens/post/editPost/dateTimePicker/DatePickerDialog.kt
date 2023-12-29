package hello.kwfriends.ui.screens.post.editPost.dateTimePicker

import android.util.Log
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import hello.kwfriends.ui.screens.post.editPost.EditPostViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerPopup(
    onDismiss: () -> Unit,
    editPostViewModel: EditPostViewModel
) {
    val dateTime = LocalDateTime.now()

    val datePickerState = remember {
        DatePickerState(
            initialDisplayedMonthMillis = null,
            yearRange = (2023..2040), // KW Friends 서비스 종료 시점으로 설정
            initialDisplayMode = DisplayMode.Picker,
            initialSelectedDateMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
        )
    }

    DatePickerDialog(
        shape = RoundedCornerShape(6.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                editPostViewModel.date = datePickerState.selectedDateMillis!!
                editPostViewModel.datePickerPopupState = false
                Log.d("confirmButton", editPostViewModel.date.toString())
            }
            ) {
                Text(text = "확인", fontFamily = FontFamily.Default)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "취소", fontFamily = FontFamily.Default)
            }
        },
    ) {
        DatePicker(
            title = { Text(text = "모임 날짜를 정해주세요") },
            headline = {
                Text(
                    text = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(
                        editPostViewModel.date
                    )
                )
            },
            state = datePickerState,
            dateValidator = { timestamp ->
                timestamp > Instant.now().toEpochMilli()
            },
            dateFormatter = DatePickerFormatter(yearSelectionSkeleton = "yyyy/MM/dd")
        )
    }
}
