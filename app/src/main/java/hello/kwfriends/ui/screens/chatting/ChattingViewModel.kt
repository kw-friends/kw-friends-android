package hello.kwfriends.ui.screens.chatting

import android.net.Uri
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
import hello.kwfriends.firebase.storage.ChattingImage
import hello.kwfriends.firebase.storage.ProfileImage
import kotlinx.coroutines.launch

class ChattingViewModel : ViewModel() {

    var messageData by mutableStateOf<Map<String, MessageDetail>?>(mutableMapOf())
    var roomInfo by mutableStateOf<RoomDetail?>(null)

    var inputChatting by mutableStateOf<String>("")

    var chattingImageUri by mutableStateOf<Uri?>(null)
    var imagePopupUri by mutableStateOf<String?>(null)
    var showSideSheet by mutableStateOf<Boolean>(false)

    fun setInputChattingText(text: String) {
        inputChatting = text
    }

    fun sendMessage(roomID: String) {
        if (chattingImageUri != null) {
            val uri = chattingImageUri
            chattingImageUri = null
            viewModelScope.launch {
                Chattings.sendImageMessage(
                    roomID = roomID,
                    uri = uri!!
                )
            }
        }
        if (inputChatting != "") {
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
        messageData = emptyMap()
        Chattings.addMessageListener(roomID) {
            messageData = messageData?.toMutableMap().apply {
                this?.set(it.messageID, it)
                viewModelScope.launch {
                    if (it.type == MessageType.IMAGE) {
                        ChattingImage.updateChattingUriMap(
                            it.content,
                            ChattingImage.getDownloadUrl(it.content)
                        )
                    }
                }
            }
        }
    }

    fun removeMessage(roomID: String, messageID: String) {
        viewModelScope.launch {
            Chattings.removeMessage(
                roomID = roomID,
                messageID = messageID
            )
        }
    }

    fun leaveCattingRoom(roomID: String) {
        viewModelScope.launch {
            Chattings.leave(roomID)
        }
    }
}