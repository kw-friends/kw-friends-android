package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Report {
    var database = Firebase.database.reference


    suspend fun report(postID: String, reporterID: String, reason: List<String>): Boolean {
        val key = database.child("reports").push().key
        val reportMap = mapOf(
            "reports/$key/postID" to postID,
            "reports/$key/reporterID" to reporterID,
            "reports/$key/reason" to reason,
            "reports/$key/timestamp" to ServerValue.TIMESTAMP,
            "posts/$postID/reporters/$reporterID" to true
        )
        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(reportMap)
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