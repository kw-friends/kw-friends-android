package hello.kwfriends.ui.screens.chatting

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import kotlinx.coroutines.launch

class ChattingViewModel: ViewModel() {

    var chattingData by mutableStateOf<Map<String, Map<String, Any>>?>(emptyMap())

    fun getMessages(roomID: String) {
        viewModelScope.launch {
            chattingData = Chattings.getRoomMessages(roomID)
            Log.w("test", "${chattingData}")

        }
    }

}