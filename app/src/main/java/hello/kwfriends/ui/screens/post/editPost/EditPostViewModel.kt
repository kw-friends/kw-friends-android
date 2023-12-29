package hello.kwfriends.ui.screens.post.editPost

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ServerValue
import hello.kwfriends.Tags.Tags
import hello.kwfriends.firebase.authentication.UserAuth
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.Post
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.ui.screens.post.postValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class EditPostViewModel : ViewModel() {

    var postID by mutableStateOf("")

    var gatheringTitle by mutableStateOf("")
    var gatheringTitleStatus by mutableStateOf(false)

    var gatheringPromoterUID by mutableStateOf("")
    var gatheringPromoter by mutableStateOf("")

    var gatheringTime by mutableLongStateOf(0L)
    var gatheringTimeValidation by mutableStateOf(false)
    var date by mutableLongStateOf(0L)
    var gatheringHour by mutableStateOf("")
    var gatheringMinute by mutableStateOf("")

    var gatheringLocation by mutableStateOf("")

    var maximumParticipants by mutableStateOf("")
    var participantsRangeValidation by mutableStateOf(false)

    var gatheringDescription by mutableStateOf("")
    var gatheringDescriptionStatus by mutableStateOf(false)

    var participants by mutableStateOf<MutableMap<String, Boolean>>(mutableMapOf())


    var tagMap by mutableStateOf<MutableMap<String, Boolean>>(mutableMapOf())
    var isUploading by mutableStateOf(false)


    val _snackbarEvent = MutableStateFlow<String?>(null)
    val snackbarEvent: StateFlow<String?> get() = _snackbarEvent

    var datePickerPopupState by mutableStateOf(false)

    fun onHourChanged(hour: String) {
        if (postValidation.checkHourRange(hour)) {
            gatheringHour = hour
            updateGatheringTime()
        }
    }

    fun onMinuteChanged(minute: String) {
        if (postValidation.checkMinuteRange(minute)) {
            gatheringMinute = minute
            updateGatheringTime()
        }
    }

    fun updateGatheringTime() {
        if (gatheringHour != "" && gatheringMinute != "") {
            gatheringTime =
                date + gatheringHour.toLong() * 3600000 + gatheringMinute.toLong() * 60000
            gatheringTimeValidation = true
        } else {
            gatheringTimeValidation = false
        }
    }

    fun showSnackbar(message: String) {
        _snackbarEvent.value = message
    }

    fun initPostData(postDetail: PostDetail) {
        val dateTime = LocalDateTime.now()

        postID = postDetail.postID
        gatheringTitle = postDetail.gatheringTitle
        gatheringTitleStatus = true
        gatheringPromoterUID = postDetail.gatheringPromoterUID
        gatheringPromoter = UserData.myInfo!!["name"].toString()
        gatheringTime = postDetail.gatheringTime
        gatheringLocation = ""
        gatheringDescription = postDetail.gatheringDescription
        gatheringDescriptionStatus = true
        maximumParticipants = postDetail.maximumParticipants
        participantsRangeValidation = true
        tagMap = tagMap.toMutableMap().apply {
            Tags.list.forEach { tag ->
                this[tag] = tag in postDetail.gatheringTags
            }
        }
        participants = postDetail.participants.mapKeys { it.key }.mapValues { it.value as Boolean }
            .toMutableMap()

        date = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun gatheringTitleChange(text: String) {
        gatheringTitle = text
        gatheringTitleStatus = postValidation.isStrHasData(text)
    }

    fun gatheringDescriptionChange(text: String) {
        gatheringDescription = text
        gatheringDescriptionStatus = postValidation.isStrHasData(text)
    }

    fun gatheringLocationChange() {

    }

    fun maximumParticipantsChange(max: String) {
        maximumParticipants = max
        participantsRangeValidation = postValidation.checkParticipantsRange("1", max)
    }

    fun validateGatheringInfo(): Boolean {
        return (gatheringTitleStatus &&
                gatheringDescriptionStatus &&
                participantsRangeValidation &&
                gatheringTimeValidation)
    }

    fun updateTagMap(tag: String) {
        tagMap = tagMap.toMutableMap().apply {
            this[tag] = !(tagMap[tag] ?: true)
        }
    }

    fun updatePostInfoToFirestore(end: () -> Unit) {
        showSnackbar("모임 정보 업데이트 중...")

        Log.d("NewPostViewModel", "validateGatheringInfo = ${validateGatheringInfo()}")
        Log.d("NewPostViewModel", "gatheringTitle = $gatheringTitleStatus")
        Log.d("NewPostViewModel", "gatheringPromoter = $gatheringPromoter")
        Log.d("NewPostViewModel", "maximumMemberCount = $participantsRangeValidation")
        Log.d("gatheringDescription", "gatheringDescription = $gatheringDescription")
        Log.d("participants", "participants = $participants")

        if (validateGatheringInfo()) { //항상 true?
            viewModelScope.launch {
                isUploading = true
                val result = Post.update(
                    postData = PostDetail(
                        gatheringTitle = gatheringTitle,
                        gatheringPromoter = gatheringPromoter,
                        gatheringPromoterUID = UserAuth.fa.uid.toString(),
                        gatheringLocation = gatheringLocation,
                        gatheringTime = gatheringTime,
                        maximumParticipants = maximumParticipants,
                        gatheringDescription = gatheringDescription,
                        myParticipantStatus = ParticipationStatus.PARTICIPATED,
                        timestamp = ServerValue.TIMESTAMP,
                        gatheringTags = tagMap.filter { it.value }.map { it.key },
                        participants = participants
                    ).toMap(),
                    postID = postID,
                    participants = participants
                )
                isUploading = false
                end()
            }
        } else {
            Log.w("EditPostViewModel", "부족한 정보가 있습니다.")
        }
    }

}