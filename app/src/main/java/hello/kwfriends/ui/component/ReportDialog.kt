package hello.kwfriends.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hello.kwfriends.ui.screens.home.HomeViewModel

@Composable
fun ReportDialog(homeViewModel: HomeViewModel) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        title = { Text(text = "신고 사유 선택", fontSize = 20.sp) },
        text = {
            Column {
                for(reportText in homeViewModel.reportTextList) {
                    CheckboxStyle2(
                        modifier = Modifier.fillMaxWidth(),
                        clickablePadding = 8.dp,
                        text = reportText,
                        textColor = Color.Black,
                        fontSize = 17.sp,
                        checkBoxSize = 17.dp,
                        checked = reportText in homeViewModel.reportChoice,
                        onClicked = {
                            if(reportText in homeViewModel.reportChoice) {
                                homeViewModel.reportChoice = ArrayList(homeViewModel.reportChoice).apply { remove(reportText) }
                            }
                            else {
                                homeViewModel.reportChoice = ArrayList(homeViewModel.reportChoice).apply { add(reportText) }
                            }
                        }
                    )
                }
            }
        },
        onDismissRequest = { homeViewModel.reportDialogState = false to null },
        confirmButton = {
            TextButton(
                onClick = { homeViewModel.report() },
                enabled = homeViewModel.reportChoice.isNotEmpty(),
            ) {
                Text(text = "신고", color = if(homeViewModel.reportChoice.isEmpty()) Color.Gray else Color.Black )
            }
        },
        dismissButton = {
            TextButton(onClick = { homeViewModel.reportDialogState = false to null }) {
                Text(text = "취소")
            }
        }
    )
}