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
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("setUserData", "serValue실패: $it")
                    continuation.resume(false)
                }
        }
        return result
    }

    suspend fun get(){

    }
}