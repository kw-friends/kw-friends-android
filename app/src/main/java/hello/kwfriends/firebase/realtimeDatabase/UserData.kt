package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.authentication.UserAuth
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object UserData {
    var database = Firebase.database.reference
    var myInfo: Map<String, Any>? = null
    var dataListenerAdded: Boolean = false
    var usersDataMap by mutableStateOf<MutableMap<String, Map<String, Any>?>>(mutableMapOf())

    fun updateUsersDataMap(uid: String, data: Map<String, Any>?) {
        usersDataMap = usersDataMap.toMutableMap().apply {
            this[uid] = data
        }
    }


    val userInfoListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            myInfo = dataSnapshot.getValue<Map<String, Any>>()
            Log.w("userInfoListener", "유저 정보 변경 감지됨 $myInfo")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("userInfoListener", "유저 정보 불러오기 Cancelled", databaseError.toException())
        }
    }

    //유저 정보 리스너 추가 함수
    fun addListener() {
        dataListenerAdded = true
        Log.w("UserData", "유저 정보 리스너 추가")
        database.child("users").child(UserAuth.fa.currentUser!!.uid)
            .addValueEventListener(userInfoListener)
    }

    //유저 정보 리스너 제거 함수
    fun removeListener() {
        dataListenerAdded = false
        Log.w("UserData", "유저 정보 리스너 제거")
        try {
            database.child("users").child(UserAuth.fa.currentUser!!.uid)
                .removeEventListener(userInfoListener)
        } catch (e: Exception) {
            Log.w("UserData", "유저 정보 리스너 제거 실패:", e)
        }
    }

    //맵을 updateChildren에 이용하기 위해 key에 경로를 추가해주는 함수
    fun toPath(userInfo: Map<String, Any>, uid: String): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        userInfo.forEach { (k, v) -> result["users/$uid/$k"] = v }
        return result
    }

    //realtime datastore 유저 정보 업데이트 함수
    suspend fun update(userInfo: Map<String, Any>): Boolean {
        val info = toPath(userInfo, UserAuth.fa.currentUser!!.uid)
        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(info)
                .addOnSuccessListener {
                    Log.w("update", "데이터 업데이트 성공: $userInfo")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("update", "데이터 업데이트 실패: $it")
                    continuation.resume(false)
                }
        }
        return result
    }

    //내 정보 한번만 가져와서 userInfo에 저장하는 함수
    suspend fun getMyData(): Boolean {
        val result = suspendCoroutine<Boolean> { continuation ->
            database.child("users").child(UserAuth.fa.currentUser!!.uid).get()
                .addOnSuccessListener { dataSnapshot ->
                    myInfo = dataSnapshot.getValue<Map<String, Any>>()
                    Log.w("get", "데이터 가져오기 성공 $myInfo")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("get", "데이터 가져오기 실패")
                    continuation.resume(false)
                }
                .addOnCanceledListener {
                    Log.w("get", "데이터 가져오기 캔슬")
                    continuation.resume(false)
                }
        }
        return result
    }

    //유저 정보 한번만 가져와서 반환하는 함수
    suspend fun get(uid: String): Map<String, Any>? {
        val result = suspendCoroutine<Map<String, Any>?> { continuation ->
            database.child("users").child(uid).get()
                .addOnSuccessListener { dataSnapshot ->
                    val data = dataSnapshot.getValue<Map<String, Any>>()
                    Log.w("get", "데이터 가져오기 성공 $data")
                    continuation.resume(data)
                }
                .addOnFailureListener {
                    Log.w("get", "데이터 가져오기 실패")
                    continuation.resume(null)
                }
                .addOnCanceledListener {
                    Log.w("get", "데이터 가져오기 캔슬")
                    continuation.resume(null)
                }
        }
        return result
    }
}