package hello.kwfriends.ui.screens.chatting

import android.util.Log
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

    fun getMessages(roomID: String) {
        viewModelScope.launch {
            messageData = Chattings.getRoomMessages(roomID)
            Log.w("ChattingsViewModel", "채팅 목록: ${messageData}")
        }
    }

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

    fun getRoomInfo(roomID: String) {
        viewModelScope.launch {
            roomInfo = Chattings.getRoomInfo(roomID)
        }
    }

    fun getUsersProfile() {
        viewModelScope.launch {
            val members = roomInfo?.members
            members?.keys?.forEach {
                val uri = ProfileImage.getDownloadUrl(it)
                ProfileImage.updateUsersUriMap(it, uri)
                val data = UserData.get(it)
                UserData.updateUsersDataMap(it, data)
            }
        }
    }
    fun addListener(roomID: String) {
        Chattings.addChattingListener(roomID) {
            val temp = messageData?.toMutableMap()
            temp?.set(it.messageID, it)
            messageData = temp
        }


    }
}