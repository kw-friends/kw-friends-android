package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object ServerData {
    var database = Firebase.database.reference
    var data: Map<String, Any>? = null
    var dataListenerAdded = false

    val serverDataListener = object: ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            data = dataSnapshot.getValue<Map<String, Any>>()
            Log.w("serverDataListener", "서버 정보 변경 감지됨 ${data}")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("serverDataListener", "서버 정보 불러오기 Cancelled", databaseError.toException())
        }
    }

    //서버 정보 리스너 추가 함수
    fun addListener(){
        dataListenerAdded = true
        Log.w("UserData", "서버 정보 리스너 추가")
        database.child("server").addValueEventListener(serverDataListener)
    }

    //서버 정보 리스너 제거 함수
    fun removeListener() {
        dataListenerAdded = false
        Log.w("UserData", "서버 정보 리스너 제거")
        try {
            database.child("server").removeEventListener(serverDataListener)
        }
        catch (e: Exception) {
            Log.w("UserData", "서버 정보 리스너 제거 실패:", e)
        }
    }

}