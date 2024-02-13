package hello.kwfriends.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PostReportDialog(
    state: Boolean,
    textList: List<String>,
    onDismiss: () -> Unit,
    onPostReport: (List<String>) -> Unit
) {
    //신고 선택 리스트
    var reportChoice by remember { mutableStateOf<MutableList<String>>(mutableListOf()) }
    LaunchedEffect(state) {
        if (state) {
            reportChoice = mutableListOf()
        }
    }
    if (state) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth(),
            title = {
                Text(
                    text = "신고 사유 선택",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    for (reportText in textList) {
                        CheckboxStyle2(
                            modifier = Modifier.fillMaxWidth(),
                            verticalPadding = 4.dp,
                            text = reportText,
                            textColor = Color.Black,
                            checkBoxSize = 20.dp,
                            checked = reportText in reportChoice,
                            onClicked = {
                                if (reportText in reportChoice) {
                                    reportChoice =
                                        ArrayList(reportChoice).apply { remove(reportText) }
                                } else {
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
                    onClick = { onPostReport(reportChoice) },
                    enabled = reportChoice.isNotEmpty(),
                ) {
                    Text(
                        text = "신고",
                        color = if (reportChoice.isEmpty()) Color.Gray else Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(text = "취소", style = MaterialTheme.typography.bodyMedium)
                }
            }
        )
    }
}