package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.authentication.UserAuth
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object UserData {
    var database = Firebase.database.reference

    fun toPath(userInfo: Map<String, Any>, uid: String): Map<String, Any>{
        val result = mutableMapOf<String, Any>()
        userInfo.forEach{ (k, v) -> result["users/$uid/$k"] = v }
        return result
    }
    suspend fun update(userInfo: Map<String, Any>): Boolean{
        val info = toPath(userInfo, UserAuth.fa.currentUser!!.uid)
        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(info)
                .addOnSuccessListener {
                    Log.w("update", "데이터 업데이트 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("update", "데이터 업데이트 실패: $it")
                    continuation.resume(false)
                }
        }
        return result
    }

    suspend fun get(): Map<String, Any>?{
        val result = suspendCoroutine<Map<String, Any>?> { continuation ->
            database.child("users").child(UserAuth.fa.currentUser!!.uid).get()
                .addOnSuccessListener { dataSnapshot ->
                    Log.w("get", "데이터 가져오기 성공")
                    continuation.resume(dataSnapshot.value as? Map<String, Any> ?: mapOf("not map" to "not map"))
                }
                .addOnFailureListener {
                    Log.w("get", "데이터 가져오기 실패")
                    continuation.resume(mapOf("fail" to "fail"))
                }
                .addOnCanceledListener {
                    Log.w("get", "데이터 가져오기 캔슬")
                    continuation.resume(mapOf("fail" to "fail"))
                }
        }
        return result
    }
}