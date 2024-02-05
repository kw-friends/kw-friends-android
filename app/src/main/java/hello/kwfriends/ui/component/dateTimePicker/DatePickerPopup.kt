package hello.kwfriends.ui.component.dateTimePicker

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import hello.kwfriends.ui.screens.post.setPostData.SetPostDataViewModel

@Composable
fun DatePickerPopup(
    state: Boolean,
    onDismiss: () -> Unit,
    setPostDataViewModel: SetPostDataViewModel
) {
    if (state) {
        Popup(
            onDismissRequest = onDismiss
        ) {
            BackHandler {
                onDismiss()
            }
            DatePickerPopup(
                onDismiss = onDismiss,
                setPostDataViewModel = setPostDataViewModel
            )
        }
    }
}
