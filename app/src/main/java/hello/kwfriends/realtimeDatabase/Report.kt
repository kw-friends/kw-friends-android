package hello.kwfriends.realtimeDatabase

import android.util.Log
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Report {
    var database = Firebase.database.reference


    suspend fun report(postID: String, reporterID: String, reason: List<String>): Boolean {
        val reportMap = mapOf(
            "postID" to postID,
            "reporterID" to reporterID,
            "reason" to reason,
            "timestamp" to ServerValue.TIMESTAMP
        )
        val result = suspendCoroutine<Boolean> { continuation ->
            database.child("reports").push().setValue(reportMap)
                .addOnSuccessListener {
                    Log.w("Report.report()", "신고 업로드 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Report.report()", "신고 업로드 실패(fail): $it")
                    continuation.resume(false)
                }
                .addOnCanceledListener {
                    Log.w("Report.report()", "신고 업로드 실패(cancel)")
                    continuation.resume(false)
                }
        }
        return result
    }

}