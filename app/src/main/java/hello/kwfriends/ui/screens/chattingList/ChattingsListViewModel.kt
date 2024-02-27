package hello.kwfriends.ui.screens.chattingList

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import hello.kwfriends.firebase.realtimeDatabase.ChattingRoomType
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import hello.kwfriends.firebase.realtimeDatabase.UserData
import kotlinx.coroutines.launch

class ChattingsListViewModel : ViewModel() {


    var userList: MutableList<String> = mutableStateListOf()

    //채팅방 목록 리스너 추가
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

    /*fun notificationTest(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TEST_CHANNEL"
            val descriptionText = "TEST_CHANNEL"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("TEST_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val builder = NotificationCompat.Builder(context, "TEST_CHANNEL")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(1, builder.build())
        }
    }*/

    fun notificationTest() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            viewModelScope.launch {
                UserData.update(mapOf("fcm-token" to token))
            }
            // Log and toast
            Log.d(TAG, "FCM 토큰: $token")
        })
    }


}