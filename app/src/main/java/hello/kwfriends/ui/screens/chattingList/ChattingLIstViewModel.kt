package hello.kwfriends.ui.screens.chattingList

import android.util.Log
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

class ChattingLIstViewModel : ViewModel() {

    var chattingRoomDatas by mutableStateOf<Map<String, Any>?>(mutableMapOf())

    fun getRoomList() {
        viewModelScope.launch {
            chattingRoomDatas =  Chattings.getRoomList()
            Log.w("test", "$chattingRoomDatas")
        }
    }

    fun temp_addRoom() {
        viewModelScope.launch {
            val roomID = Chattings.make(
                title = "테스트용 채팅방",
                owners = listOf(Firebase.auth.currentUser!!.uid),
                members = listOf(Firebase.auth.currentUser!!.uid)
            )
            if(roomID != null) {
                Chattings.sendMessage(
                    roomID = roomID,
                    uid = "BROADCAST",
                    content = "채팅방이 생성되었습니다",
                    type = MessageType.TEXT
                )
            }
        }
    }

    fun temp_sendMessage() {
        viewModelScope.launch {
            Chattings.sendMessage(
                roomID = chattingRoomDatas?.keys?.first()!!,
                uid = Firebase.auth.currentUser!!.uid,
                content = "안녕하세요!",
                type = MessageType.TEXT
            )
        }
    }

}