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
import kotlinx.coroutines.launch

class ChattingLIstViewModel : ViewModel() {

    var chattingRoomDatas by mutableStateOf<Map<String, Any>?>(mutableMapOf())

    fun getRoomList() {
        viewModelScope.launch {
            chattingRoomDatas =  Chattings.getRoomList()
            Log.w("test", "$chattingRoomDatas")
        }
    }

    fun addRoom() {
        viewModelScope.launch {
            Chattings.make(
                title = "테스트용 채팅방",
                owners = listOf(Firebase.auth.currentUser!!.uid),
                members = listOf(Firebase.auth.currentUser!!.uid)
            )
        }
    }

}