package hello.kwfriends.firebase.cloudMessaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import hello.kwfriends.firebase.realtimeDatabase.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KFfirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 메시지를 받았을 때 수행할 작업
        Log.w("KFfirebaseMessagingService", "메시지 받음: $remoteMessage")
    }

    override fun onNewToken(token: String) {
        // 토큰이 갱신되었을 때 수행할 작업
        CoroutineScope(Dispatchers.IO).launch {
            UserData.update(mapOf("fcm-token" to token))
        }
        Log.w("KFfirebaseMessagingService", "FCM 토큰 갱신됨: $token")

    }
}