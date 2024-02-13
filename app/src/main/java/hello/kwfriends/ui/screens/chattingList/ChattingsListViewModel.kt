package hello.kwfriends.ui.screens.chattingList

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.realtimeDatabase.ChattingRoomType
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import kotlinx.coroutines.launch

class ChattingsListViewModel : ViewModel() {


    var userList: MutableList<String> = mutableStateListOf()

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