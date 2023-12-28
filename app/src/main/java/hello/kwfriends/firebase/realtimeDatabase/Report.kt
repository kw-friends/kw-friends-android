package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Report {
    var database = Firebase.database.reference

    suspend fun userReport(uid: String, reporterID: String, reason: List<String>): Boolean {
        val key = database.child("userReports").push().key
        val reportMap = mapOf(
            "reports/user/$key/uid" to uid,
            "reports/user/$key/reporterID" to reporterID,
            "reports/user/$key/reason" to reason,
            "reports/user/$key/timestamp" to ServerValue.TIMESTAMP,
            "users/$uid/reporters/$reporterID" to true
        )
        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(reportMap)
                .addOnSuccessListener {
                    Log.w("Report.userReport()", "신고 업로드 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Report.userReport()", "신고 업로드 실패(fail): $it")
                    continuation.resume(false)
                }
                .addOnCanceledListener {
                    Log.w("Report.userReport()", "신고 업로드 실패(cancel)")
                    continuation.resume(false)
                }
        }
        return result
    }

    suspend fun postReport(postID: String, postProviderID: String, reporterID: String, reason: List<String>): Boolean {
        val key = database.child("postReports").push().key
        val reportMap = mapOf(
            "reports/post/$key/postID" to postID,
            "reports/post/$key/postPromoterID" to postProviderID,
            "reports/post/$key/reporterID" to reporterID,
            "reports/post/$key/reason" to reason,
            "reports/post/$key/timestamp" to ServerValue.TIMESTAMP,
            "posts/$postID/reporters/$reporterID" to true
        )
        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(reportMap)
                .addOnSuccessListener {
                    Log.w("Report.postReport()", "신고 업로드 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Report.postReport()", "신고 업로드 실패(fail): $it")
                    continuation.resume(false)
                }
                .addOnCanceledListener {
                    Log.w("Report.postReport()", "신고 업로드 실패(cancel)")
                    continuation.resume(false)
                }
        }
        return result
    }

}