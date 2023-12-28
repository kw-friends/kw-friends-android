package hello.kwfriends.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserReportDialog(
    state: Boolean,
    textList: List<String>,
    onDismiss: () -> Unit,
    onUserReport: (List<String>) -> Unit
) {
    //신고 선택 리스트
    var reportChoice by remember { mutableStateOf<MutableList<String>>(mutableListOf()) }
    LaunchedEffect(state) {
        if(state) {
            reportChoice = mutableListOf()
        }
    }
    if(state) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth(),
            title = { Text(text = "신고 사유 선택", fontSize = 20.sp) },
            text = {
                Column {
                    for(reportText in textList) {
                        CheckboxStyle2(
                            modifier = Modifier.fillMaxWidth(),
                            clickablePadding = 8.dp,
                            text = reportText,
                            textColor = Color.Black,
                            fontSize = 17.sp,
                            checkBoxSize = 17.dp,
                            checked = reportText in reportChoice,
                            onClicked = {
                                if(reportText in reportChoice) {
                                    reportChoice = ArrayList(reportChoice).apply { remove(reportText) }
                                }
                                else {
                                    reportChoice = ArrayList(reportChoice).apply { add(reportText) }
                                }
                            }
                        )
                    }
                }
            },
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(
                    onClick = { onUserReport(reportChoice) },
                    enabled = reportChoice.isNotEmpty(),
                ) {
                    Text(text = "신고", color = if(reportChoice.isEmpty()) Color.Gray else Color.Black )
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(text = "취소")
                }
            }
        )
    }
}