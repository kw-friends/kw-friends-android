package hello.kwfriends.ui.screens.post.setPostData

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
import hello.kwfriends.firebase.realtimeDatabase.Action
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.Post
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.ui.screens.post.postValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class SetPostDataViewModel : ViewModel() {

    var postID by mutableStateOf("")

    var gatheringTitle by mutableStateOf("")
    var gatheringTitleValidation by mutableStateOf(false)

    var gatheringPromoterUID by mutableStateOf("")
    var gatheringPromoter by mutableStateOf("")

    var gatheringTimeUse by mutableStateOf(false)

    var gatheringTime by mutableLongStateOf(0L)
    var gatheringTimeValidation by mutableStateOf(false)
    var gatheringDate by mutableLongStateOf(0L)
    var gatheringHour by mutableStateOf("")
    var gatheringMinute by mutableStateOf("")
    var gatheringTimeMessage by mutableStateOf("")

    var gatheringLocationUse by mutableStateOf(false)
    var gatheringLocation by mutableStateOf("")
    var gatheringLocationValidation by mutableStateOf(false)

    var maximumParticipants by mutableStateOf("")
    var maximumParticipantsValidation by mutableStateOf(false)

    var gatheringDescription by mutableStateOf("")
    var gatheringDescriptionValidation by mutableStateOf(false)

    var participants by mutableStateOf<MutableMap<String, Boolean>>(mutableMapOf())


    var tagMap by mutableStateOf<MutableMap<String, Boolean>>(mutableMapOf())

    var isUploading by mutableStateOf(false)


    val _snackbarEvent = MutableStateFlow<String?>(null)
    val snackbarEvent: StateFlow<String?> get() = _snackbarEvent

    var datePickerPopupState by mutableStateOf(false)

    fun showSnackbar(message: String) {
        _snackbarEvent.value = message
    }

    fun initPostData(postDetail: PostDetail?, state: Action) {
        Log.d("actionState", state.toString())

        if (state == Action.MODIFY && postDetail != null) {
            postID = postDetail.postID
            gatheringTitle = postDetail.gatheringTitle
            gatheringTitleValidation = true
            gatheringPromoterUID = postDetail.gatheringPromoterUID
            gatheringPromoter = UserData.myInfo!!["name"].toString()
            gatheringTime = postDetail.gatheringTime
            gatheringLocation = postDetail.gatheringLocation
            gatheringDescription = postDetail.gatheringDescription
            gatheringDescriptionValidation = true
            maximumParticipants = postDetail.maximumParticipants
            maximumParticipantsValidation = true
            tagMap = tagMap.toMutableMap().apply {
                Tags.list.forEach { tag ->
                    this[tag] = tag in postDetail.gatheringTags
                }
            }
            participants =
                postDetail.participants.mapKeys { it.key }.mapValues { it.value as Boolean }
                    .toMutableMap()

            val time = Instant.ofEpochMilli(postDetail.gatheringTime).atZone(ZoneId.systemDefault())
            gatheringDate =
                if (gatheringTime == 0L) LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli() else gatheringTime
            gatheringHour = DateTimeFormatter.ofPattern("HH").format(time).toString()
            gatheringMinute = DateTimeFormatter.ofPattern("mm").format(time).toString()
            Log.d("dateInit", gatheringDate.toString())
            validateGatheringTime()

            gatheringTimeUse = gatheringTime != 0L
            gatheringLocationUse = gatheringLocation != ""

            gatheringTimeValidation = gatheringTimeUse
            gatheringLocationValidation = gatheringLocationUse
        }

        if (state == Action.ADD) {
            gatheringDate =
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            gatheringPromoter = UserData.myInfo!!["name"].toString()
            gatheringTitle = ""
            gatheringTitleValidation = false
            gatheringTime = 0L
            gatheringTimeMessage = "입력칸이 비어있습니다."
            gatheringTimeValidation = false
            gatheringLocationValidation = false
            gatheringLocation = ""
            gatheringDescription = ""
            gatheringDescriptionValidation = false
            maximumParticipants = ""
            maximumParticipantsValidation = false
            gatheringHour = ""
            gatheringMinute = ""
            tagMap = tagMap.toMutableMap().apply {
                Tags.list.forEach { tag ->
                    this[tag] = false
                }
            }

            gatheringTimeUse = false
            gatheringLocationUse = false
        }
    }

    fun gatheringTitleChange(text: String) {
        gatheringTitle = text
        gatheringTitleValidation = postValidation.isStrHasData(text)
    }

    fun gatheringDescriptionChange(text: String) {
        gatheringDescription = text
        gatheringDescriptionValidation = postValidation.isStrHasData(text)
    }

    fun onHourChanged(hour: String) {
        if (postValidation.checkHourRange(hour)) {
            gatheringHour = hour
            validateGatheringTime()
        }
    }

    fun onMinuteChanged(minute: String) {
        if (postValidation.checkMinuteRange(minute)) {
            gatheringMinute = minute
            validateGatheringTime()
        }
    }

    fun onDateChanged(time: Long) {
        gatheringDate = time
        Log.d("dateValidator", gatheringDate.toString())
        validateGatheringTime()
    }

    fun getTimeZoneOffset(): Long {
        val timeZone = TimeZone.getDefault()
        val timeOffset = timeZone.rawOffset.toLong()
        Log.d("getTimeZoneOffset", "$timeOffset")
        return timeOffset
    }

    // 모임 시간이 현재 시간 이후인지 확인
    fun validateGatheringTime() {
        val minimumTimeHour: Int = 3 // 시간 단위
        val timeDelay: Long = minimumTimeHour.toLong() * 3600000

        if (gatheringHour == "" || gatheringMinute == "") {
            gatheringTimeMessage = "입력칸이 비어있습니다."
            gatheringTimeValidation = false
            return
        }

        gatheringTime =
            gatheringDate + gatheringHour.toLong() * 3600000 + gatheringMinute.toLong() * 60000 - getTimeZoneOffset()
        Log.d("gatheringTime", gatheringTime.toString())
        val liveDateTime =
            ZonedDateTime.now(ZoneId.systemDefault()).toInstant().toEpochMilli()
        Log.d("liveDateTime", liveDateTime.toString())

        if (gatheringTime >= liveDateTime + timeDelay) {
            gatheringTimeValidation = true
            gatheringTimeMessage = ""
        } else {
            gatheringTimeValidation = false
            gatheringTimeMessage = "최소 ${minimumTimeHour}시간 이후 시점을 설정해 주세요."
        }
    }

    fun gatheringLocationChange(text: String) {
        gatheringLocation = text
        validateGatheringLocation()
    }

    fun validateGatheringLocation() {
        gatheringLocationValidation = gatheringLocation != ""
    }

    fun maximumParticipantsChange(max: String) {
        maximumParticipants = max
        maximumParticipantsValidation = postValidation.checkMaximumParticipantsRange(max)
    }

    fun validateGatheringInfo(): Boolean {
        return gatheringTitleValidation && // 모임 제목
                gatheringDescriptionValidation && // 모임 인원 범위
                maximumParticipantsValidation && // 최대 인원
                ((!gatheringTimeUse) || (gatheringTimeUse && gatheringTimeValidation)) && // 모임 시간
                ((!gatheringLocationUse) || (gatheringLocationUse && gatheringLocationValidation)) // 모임 위치
    }

    fun updateTagMap(tag: String) {
        tagMap = tagMap.toMutableMap().apply {
            this[tag] = !(tagMap[tag] ?: true)
        }
    }

    fun updatePostInfoToFirestore(end: () -> Unit) {
        showSnackbar("모임 정보 업데이트 중...")

        Log.d("NewPostViewModel", "validateGatheringInfo = ${validateGatheringInfo()}")
        Log.d("NewPostViewModel", "gatheringTitle = $gatheringTitleValidation")
        Log.d("NewPostViewModel", "gatheringPromoter = $gatheringPromoter")
        Log.d("NewPostViewModel", "maximumMemberCount = $maximumParticipantsValidation")
        Log.d("gatheringDescription", "gatheringDescription = $gatheringDescription")
        Log.d("participants", "participants = $participants")

        if (!gatheringTimeUse) {
            gatheringTime = 0L
        }

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

    fun uploadPostInfoToFirestore(end: () -> Unit) {
        showSnackbar("모임 생성 중...")

        Log.w("NewPostViewModel", "validateGatheringInfo = ${validateGatheringInfo()}")
        Log.w("NewPostViewModel", "gatheringTitle = $gatheringTitleValidation")
        Log.w("NewPostViewModel", "gatheringPromoter = $gatheringPromoter")
        Log.w("NewPostViewModel", "maximumMemberCount = $maximumParticipantsValidation")
        Log.w("gatheringDescription", "gatheringDescription = $gatheringDescription")

        if (!gatheringTimeUse) {
            gatheringTime = 0L
        }

        if (validateGatheringInfo()) { //항상 true?
            viewModelScope.launch {
                isUploading = true
                val result = Post.upload(
                    PostDetail(
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