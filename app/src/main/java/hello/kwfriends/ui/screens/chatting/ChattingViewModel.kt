package hello.kwfriends.ui.screens.chatting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import hello.kwfriends.firebase.realtimeDatabase.MessageType
import kotlinx.coroutines.launch

class ChattingViewModel : ViewModel() {

    var messageData by mutableStateOf<Map<String, Map<String, Any>>?>(emptyMap())

    var inputChatting by mutableStateOf<String>("")

    fun setInputChattingText(text: String) { inputChatting = text }

    fun getMessages(roomID: String) {
        viewModelScope.launch {
            messageData = Chattings.getRoomMessages(roomID)
        }
    }

    fun sendMessage(roomID: String) {
        viewModelScope.launch {
            Chattings.sendMessage(
                roomID = roomID,
                uid = Firebase.auth.currentUser!!.uid,
                content = inputChatting,
                type = MessageType.TEXT
            )
            inputChatting = ""
            messageData = Chattings.getRoomMessages(roomID)
        }
    }

}