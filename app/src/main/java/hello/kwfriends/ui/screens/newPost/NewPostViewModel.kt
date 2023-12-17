package hello.kwfriends.ui.screens.newPost

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.firebase.realtimeDatabase.Post
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.Tags.Tags
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewPostViewModel : ViewModel() {
    var gatheringTitle by mutableStateOf("")
    var gatheringTitleStatus by mutableStateOf(false)

    var gatheringPromoter by mutableStateOf("")

    var gatheringTime by mutableStateOf("")

    var gatheringLocation by mutableStateOf("")

    var maximumParticipants by mutableStateOf("")
    var participantsRangeValidation by mutableStateOf(false)

    var gatheringDescription by mutableStateOf("")
    var gatheringDescriptionStatus by mutableStateOf(false)

    var isUploading by mutableStateOf(false)
    var uploadResult by mutableStateOf(true)
    val _snackbarEvent = MutableStateFlow<String?>(null)
    val snackbarEvent: StateFlow<String?> get() = _snackbarEvent

    //태그 저장 변수
    var tagMap = mutableStateMapOf<String, Boolean>()

    fun showSnackbar(message: String) {
        _snackbarEvent.value = message
    }

    fun isStrHasData(text: String?): Boolean {
        Log.d("isStrHasData()", (text == null || text == "").toString())
        return !(text == null || text == "")
    }

    fun checkParticipantsRange(
        min: String,
        max: String
    ): Boolean { // min이 1이상, max가 2 이상, 100 이하이고, min < max인지를 확인
        return try {
            Log.i("checkParticipantsRange", "true")
            min.toInt() < max.toInt() && min.toInt() in 1..100 && max.toInt() in 2..100
        } catch (e: NumberFormatException) {
            Log.i("checkParticipantsRange", "false")
            false
        }
    }

    fun gatheringTitleChange(text: String) {
        gatheringTitle = text
        gatheringTitleStatus = isStrHasData(text)
    }

    fun gatheringDescriptionChange(text: String) {
        gatheringDescription = text
        gatheringDescriptionStatus = isStrHasData(text)
    }

    fun maximumParticipantsChange(max: String) {
        maximumParticipants = max
        participantsRangeValidation = checkParticipantsRange("1", max)
    }

    fun validateGatheringInfo(): Boolean {
        return (gatheringTitleStatus &&
                gatheringDescriptionStatus &&
                participantsRangeValidation)
    }

    fun uploadResultUpdate(result: Boolean) {
        uploadResult = result
    }

    fun initInput() {
        gatheringPromoter = UserData.userInfo!!["name"].toString()
        gatheringTitle = ""
        gatheringTitleStatus = false
        gatheringTime = ""
        gatheringLocation = ""
        gatheringDescription = ""
        gatheringDescriptionStatus = false
        maximumParticipants = ""
        participantsRangeValidation = false
        tagMap = mutableStateMapOf<String, Boolean>().apply {
            Tags.list.forEach { tag ->
                this[tag] = false
            }
        }
    }

    fun uploadGatheringToFirestore(end: () -> Unit) {
        showSnackbar("모임 생성 중...")

        Log.w("NewPostViewModel", "validateGatheringInfo = ${validateGatheringInfo()}")
        Log.w("NewPostViewModel", "gatheringTitle = $gatheringTitleStatus")
        Log.w("NewPostViewModel", "gatheringPromoter = $gatheringPromoter")
        Log.w("NewPostViewModel", "maximumMemberCount = $participantsRangeValidation")
        Log.w("gatheringDescription", "gatheringDescription = $gatheringDescription")

        if (validateGatheringInfo()) { //항상 true?
            viewModelScope.launch {
                isUploading = true
                val result = Post.upload(
                    PostDetail(
                        gatheringTitle = gatheringTitle,
                        gatheringPromoterUID = gatheringPromoter,
                        gatheringLocation = gatheringLocation,
                        gatheringTime = gatheringTime,
                        maximumParticipants = maximumParticipants,
                        gatheringDescription = gatheringDescription,
                        myParticipantStatus = ParticipationStatus.PARTICIPATED,
                        gatheringTags = tagMap.filter { it.value }.map { it.key },
                        postID = "123"
                    ).toMap()
                )
                end()
                isUploading = false
            }
        } else {
            Log.w("NewPostViewModel", "부족한 정보가 있습니다.")
        }
    }
}