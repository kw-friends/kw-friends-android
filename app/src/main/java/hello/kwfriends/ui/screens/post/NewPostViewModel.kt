package hello.kwfriends.ui.screens.post

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hello.kwfriends.firebase.firestoreManager.PostManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewPostViewModel : ViewModel() {
    var gatheringTitle by mutableStateOf("")
    var gatheringTitleStatus by mutableStateOf(false)

    var gatheringPromoter by mutableStateOf("User")

    var gatheringTime by mutableStateOf("")
    var gatheringTimeStatus by mutableStateOf(false)

    var gatheringLocation by mutableStateOf("")
    var gatheringLocationStatus by mutableStateOf(false)

    var maximumMemberCount by mutableStateOf("")
    var maximumMemberCountStatus by mutableStateOf(false)

    var gatheringDescription by mutableStateOf("")

    var isUploading by mutableStateOf(false)
    var uploadResult by mutableStateOf(true)
    val _snackbarEvent = MutableStateFlow<String?>(null)
    val snackbarEvent: StateFlow<String?> get() = _snackbarEvent

    fun showSnackbar(message: String) {
        _snackbarEvent.value = message
    }

    fun isStrHasData(text: String?): Boolean {
        Log.d("isStrHasData()", (text == null || text == "").toString())
        return !(text == null || text == "")
    }

    // 입력받은 텍스트의 Null 여부 확인 후, Int 변환 값의 크기 비교
    fun isInRange(text: String?, compare: String = "", target: Int = 0): Boolean {
        return if (text == null || text == "") {
            false
        } else {
            try {
                if (compare == "bigger") {
                    text.toInt() >= target
                } else if (compare == "smaller") {
                    text.toInt() <= target
                } else {
                    false
                }
            } catch (e: NumberFormatException) {
                return false
            }
        }
    }

    fun gatheringTitleChange(text: String) {
        gatheringTitle = text
        gatheringTitleStatus = isStrHasData(text)
    }

    fun gatheringTimeChange(text: String) {
        gatheringTime = text
        gatheringTimeStatus = isStrHasData(text)
    }

    fun gatheringLocationChange(text: String) {
        gatheringLocation = text
        gatheringLocationStatus = isStrHasData(text)
    }

    fun gatheringDescriptionChange(text: String) {
        gatheringDescription = text
    }

    fun maximumMemberCountChange(text: String) {
        maximumMemberCount = text
        maximumMemberCountStatus = isInRange(text, "bigger", 2)
    }

    fun validateGatheringInfo(): Boolean {
        return (gatheringTitleStatus &&
                gatheringLocationStatus &&
                gatheringTimeStatus &&
                maximumMemberCountStatus)
    }

    fun uploadResultUpdate(result: Boolean) {
        uploadResult = result
    }


    fun uploadGatheringToFirestore() {
        showSnackbar("모임 생성 중...")

        Log.w("NewPostViewModel", "validateGatheringInfo = ${validateGatheringInfo()}")
        Log.w("NewPostViewModel", "gatheringTitle = $gatheringTitleStatus")
        Log.w("NewPostViewModel", "gatheringPromoter = $gatheringPromoter")
        Log.w("NewPostViewModel", "gatheringLocation = $gatheringLocationStatus")
        Log.w("NewPostViewModel", "gatheringTime = $gatheringTimeStatus")
        Log.w("NewPostViewModel", "maximumMemberCount = $maximumMemberCountStatus")
        Log.w("gatheringDescription", "gatheringDescription = $gatheringDescription")

        if (validateGatheringInfo()) { //항상 true?
            viewModelScope.launch {
                isUploading = true
                PostManager.uploadPost(
                    gatheringTitle,
                    gatheringPromoter,
                    gatheringLocation,
                    gatheringTime,
                    maximumMemberCount,
                    gatheringDescription,
                    this@NewPostViewModel

                )
                isUploading = false
            }
        } else {
            Log.w("NewPostViewModel", "부족한 정보가 있습니다.")
        }

    }
}