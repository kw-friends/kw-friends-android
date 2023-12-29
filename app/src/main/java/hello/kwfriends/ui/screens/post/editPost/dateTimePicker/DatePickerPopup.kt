package hello.kwfriends.ui.screens.post.editPost.dateTimePicker

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import hello.kwfriends.ui.screens.post.editPost.EditPostViewModel

@Composable
fun DatePickerPopup(
    state: Boolean,
    onDismiss: () -> Unit,
    editPostViewModel: EditPostViewModel
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
                editPostViewModel = editPostViewModel
            )
        }
    }
}
