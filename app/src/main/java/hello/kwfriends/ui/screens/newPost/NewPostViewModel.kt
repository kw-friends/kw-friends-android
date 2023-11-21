package hello.kwfriends.ui.screens.newPost

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.PostDetail_
import hello.kwfriends.firebase.realtimeDatabase.Post_
import hello.kwfriends.ui.main.Routes
import hello.kwfriends.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewPostViewModel : ViewModel() {
    var gatheringTitle by mutableStateOf("")
    var gatheringTitleStatus by mutableStateOf(false)

    var gatheringPromoter by mutableStateOf(AuthViewModel.userInfo!!["name"].toString())

    var gatheringTime by mutableStateOf("")
    var gatheringTimeStatus by mutableStateOf(false)

    var gatheringLocation by mutableStateOf("")
    var gatheringLocationStatus by mutableStateOf(false)

    var maximumParticipants by mutableStateOf("")
    var minimumParticipants by mutableStateOf("")
    var participantsRangeValidation by mutableStateOf(false)

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

    fun checkParticipantsRange(
        min: String,
        max: String
    ): Boolean { // mix와 max가 2 이상, 100 이하이고, min < max인지를 확인
        return try {
            Log.i("checkParticipantsRange", "true")
            min.toInt() < max.toInt() && min.toInt() in 2..100 && max.toInt() in 2..100
        } catch (e: NumberFormatException) {
            Log.i("checkParticipantsRange", "false")
            false
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

    fun maximumParticipantsChange(min: String = minimumParticipants, max: String) {
        maximumParticipants = max
        participantsRangeValidation = checkParticipantsRange(min, max)
    }

    fun minimumParticipantsChange(min: String, max: String = maximumParticipants) {
        minimumParticipants = min
        participantsRangeValidation = checkParticipantsRange(min, max)
    }

    fun validateGatheringInfo(): Boolean {
        return (gatheringTitleStatus &&
                gatheringLocationStatus &&
                gatheringTimeStatus &&
                participantsRangeValidation)
    }

    fun uploadResultUpdate(result: Boolean) {
        uploadResult = result
    }


    fun uploadGatheringToFirestore(navigation: NavController) {
        showSnackbar("모임 생성 중...")

        Log.w("NewPostViewModel", "validateGatheringInfo = ${validateGatheringInfo()}")
        Log.w("NewPostViewModel", "gatheringTitle = $gatheringTitleStatus")
        Log.w("NewPostViewModel", "gatheringPromoter = $gatheringPromoter")
        Log.w("NewPostViewModel", "gatheringLocation = $gatheringLocationStatus")
        Log.w("NewPostViewModel", "gatheringTime = $gatheringTimeStatus")
        Log.w("NewPostViewModel", "maximumMemberCount = $participantsRangeValidation")
        Log.w("gatheringDescription", "gatheringDescription = $gatheringDescription")

        if (validateGatheringInfo()) { //항상 true?
            viewModelScope.launch {
                isUploading = true
                val result = Post_.upload(
                    PostDetail_(
                        gatheringTitle= gatheringTitle,
                        gatheringPromoter = gatheringPromoter,
                        gatheringLocation = gatheringLocation,
                        gatheringTime = gatheringTime,
                        maximumParticipants = maximumParticipants,
                        minimumParticipants = minimumParticipants,
                        gatheringDescription = gatheringDescription,
                        participantStatus = ParticipationStatus.PARTICIPATED
                    ).toMap()
                )
                navigation.navigate(Routes.HOME_SCREEN)
                isUploading = false
            }
        } else {
            Log.w("NewPostViewModel", "부족한 정보가 있습니다.")
        }
    }
}