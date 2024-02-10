package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class NoticeDetail(
    var title: String = "",
    var content: String = "",
    var uploadedTime: Long = 0L
)

object Notice {
    var isNoticeLoading by mutableStateOf(true)
    private var database = Firebase.database
    var noticeDetails = mutableListOf<NoticeDetail>()

    suspend fun initNotices(): List<NoticeDetail> {
        noticeDetails = emptyList<NoticeDetail>().toMutableList()
        val noticeFetcher = database.reference.child("server/notices").get()
            .addOnSuccessListener {
                Log.d("initNotices", "공지사항 가져오기 성공: $it")
            }
            .addOnFailureListener { e ->
                Log.d("initNotices", "공지사항 가져오기 실패: $e")
            }.await()

        for (noticeSnapshot in noticeFetcher.children) {
            val noticeDetail = noticeSnapshot.getValue(NoticeDetail::class.java)
            noticeDetail?.let { noticeDetails.add(it) }
        }

        Log.d("initNotices", "$noticeDetails")

        noticeDetails.sortByDescending { it.uploadedTime }

        return noticeDetails.toList()
    }
}