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
            "userReports/$key/uid" to uid,
            "userReports/$key/reporterID" to reporterID,
            "userReports/$key/reason" to reason,
            "userReports/$key/timestamp" to ServerValue.TIMESTAMP,
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
            "postReports/$key/postID" to postID,
            "postReports/$key/postProviderID" to postProviderID,
            "postReports/$key/reporterID" to reporterID,
            "postReports/$key/reason" to reason,
            "postReports/$key/timestamp" to ServerValue.TIMESTAMP,
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