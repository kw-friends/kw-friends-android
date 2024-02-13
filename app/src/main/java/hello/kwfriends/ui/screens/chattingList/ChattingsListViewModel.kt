package hello.kwfriends.ui.screens.chattingList

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.realtimeDatabase.ChattingRoomType
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import hello.kwfriends.firebase.realtimeDatabase.MessageType
import hello.kwfriends.firebase.realtimeDatabase.RoomDetail
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import kotlinx.coroutines.launch

class ChattingsListViewModel : ViewModel() {

    var sortedData: MutableList<MutableMap.MutableEntry<String, RoomDetail>>? = mutableListOf()

    var userList: MutableList<String> = mutableStateListOf()

    fun getRoomListAndProfiles() {
        viewModelScope.launch {
            Chattings.getRoomList()
            sortedData = Chattings.chattingRoomList?.entries?.sortedByDescending {
                if (it.value.recentMessage.timestamp.toString() == "") Long.MIN_VALUE
                else it.value.recentMessage.timestamp as Long
            }?.toMutableList()
            sortedData?.forEach {
                if (it.value.type == ChattingRoomType.DIRECT) {
                    val temp = it.value.members.toMutableMap()
                    temp.remove(Firebase.auth.currentUser!!.uid)
                    val uid =
                        temp.keys.toString().slice(IntRange(1, temp.keys.toString().length - 2))
                    UserData.updateUsersDataMap(uid, UserData.get(uid))
                    ProfileImage.updateUsersUriMap(uid, ProfileImage.getDownloadUrl(uid))
                }
            }
            Log.w("ChattingsListViewModel", "채팅방 목록: $Chattings.chattingRoomDatas")

        }
    }

    fun temp_addRoom() {
        viewModelScope.launch {
            val roomID = Chattings.make(
                title = "테스트용 채팅방",
                owners = listOf(Firebase.auth.currentUser!!.uid),
                members = listOf(Firebase.auth.currentUser!!.uid),
                type = ChattingRoomType.GROUP
            )
            if (roomID != null) {
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
                roomID = Chattings.chattingRoomList?.keys?.first()!!,
                uid = Firebase.auth.currentUser!!.uid,
                content = "안녕하세요!",
                type = MessageType.TEXT
            )
        }
    }

    fun addListener() {
        userList = mutableListOf<String>()
        viewModelScope.launch {
            Chattings.chattingRoomList = mutableMapOf()
            Chattings.addRoomListListener() {
                Chattings.chattingRoomList = Chattings.chattingRoomList?.toMutableMap().apply {
                    this?.set(it.roomID, it)
                    if (it.type == ChattingRoomType.DIRECT) {
                        val temp = it.members.toMutableMap()
                        temp.remove(Firebase.auth.currentUser!!.uid)
                        val uid =
                            temp.keys.toString().slice(IntRange(1, temp.keys.toString().length - 2))
                        userList = userList.toMutableList().apply {
                            this.add(uid)
                        }
                    }
                }
            }
        }
    }
}