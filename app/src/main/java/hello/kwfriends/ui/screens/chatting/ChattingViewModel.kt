package hello.kwfriends.ui.screens.chatting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import hello.kwfriends.firebase.realtimeDatabase.MessageDetail
import hello.kwfriends.firebase.realtimeDatabase.MessageType
import hello.kwfriends.firebase.realtimeDatabase.RoomDetail
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import kotlinx.coroutines.launch

class ChattingViewModel : ViewModel() {

    var messageData by mutableStateOf<Map<String, MessageDetail>?>(mutableMapOf())
    var roomInfo by mutableStateOf<RoomDetail?>(null)

    var inputChatting by mutableStateOf<String>("")

    fun setInputChattingText(text: String) { inputChatting = text }

    fun sendMessage(roomID: String) {
        if(inputChatting != "") {
            viewModelScope.launch {
                Chattings.sendMessage(
                    roomID = roomID,
                    uid = Firebase.auth.currentUser!!.uid,
                    content = inputChatting,
                    type = MessageType.TEXT
                )
                inputChatting = ""
            }
        }
    }

    fun getRoomInfoAndUserProfile(roomID: String) {
        viewModelScope.launch {
            roomInfo = Chattings.getRoomInfo(roomID)
            getUsersProfile()
        }
    }

    suspend fun getUsersProfile() {
        val members = roomInfo?.members
        members?.keys?.forEach {
            ProfileImage.updateUsersUriMap(it, ProfileImage.getDownloadUrl(it))
            UserData.updateUsersDataMap(it, UserData.get(it))
        }
    }
    fun addListener(roomID: String) {
        Chattings.addMessageListener(roomID) {
            messageData = messageData?.toMutableMap().apply {
                this?.set(it.messageID, it)
            }
        }


    }
}