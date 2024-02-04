package hello.kwfriends.firebase.realtimeDatabase

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class EventDetail(
    val name: String = "",
    var url: String = "",
    val description: String = "",
    var eventImageUrl: Uri? = null,
    var loaded: Boolean = false
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "url" to url,
            "description" to description,
            "loaded" to loaded
        )
    }
}

object Events {
    // 이벤트 카드 로딩 중 변수
    var isEventLoading by mutableStateOf(true)

    private var database = Firebase.database
    private var storage = Firebase.storage
    var eventDetails = mutableListOf<EventDetail>()
    var eventCount = 0
    suspend fun initEventCards(): Boolean {
        val eventUrlFetcher = database.reference.child("events").get()
            .addOnSuccessListener {
                Log.d("initEventCards", "이벤트 데이터 가져오기 성공: $it")
            }
            .addOnFailureListener { e ->
                Log.d("initEventCards", "이벤트 데이터 가져오기 실패: $e")
            }.await()

        for (eventSnapshot in eventUrlFetcher.children) {
            val eventDetail = eventSnapshot.getValue(EventDetail::class.java)

            val eventImageRef = storage.reference.child("events/${eventSnapshot.key}")
            Log.d("eventImageRef", eventSnapshot.key.toString())
            val url = suspendCoroutine<Uri?> { continuation ->
                eventImageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        continuation.resume(uri)
                        Log.w("getEventImageRef", "${eventSnapshot.key}의 Url 불러오기 성공")
                    }.addOnFailureListener {
                        continuation.resume(null)
                        Log.w("getEventImageRef", "${eventSnapshot.key}의 Uri 불러오기 실패")
                    }
            }
            eventDetail?.eventImageUrl = url
            eventDetail?.let { eventDetails.add(it) }
            eventCount += 1
        }

        if (eventDetails.isEmpty()) {
            isEventLoading = false
            return false
        }

        Log.d("eventList", eventDetails.toString())
        return true
    }

    fun ckeckEventsLoadStatus() {
        isEventLoading = !eventDetails.all { it.loaded }
        Log.d("ckeckEventsLoadStatus", isEventLoading.toString())
    }

}
