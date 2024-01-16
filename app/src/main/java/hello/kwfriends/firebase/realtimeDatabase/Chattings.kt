package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Chattings {
    private var database = Firebase.database.reference

    //채팅방 만들기
    suspend fun makeRoom(title: String): Boolean {
        val key = database.child("chattings").push().key
        val chattingRoomMap = mapOf(
            "chattings/chats/$key/info/title" to title,
            "chattings/chats/$key/info/state" to "available",
            "chattings/chats/$key/members/${Firebase.auth.currentUser?.uid}" to true,
            "chattings/messages/$key/${ServerValue.TIMESTAMP}" to "채팅방이 생성되었습니다.",
        )
        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(chattingRoomMap)
                .addOnSuccessListener {
                    Log.w("Chattings.makeRoom()", "채팅방 생성 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Chattings.makeRoom()", "채팅방 생성 실패(fail): $it")
                    continuation.resume(false)
                }
                .addOnCanceledListener {
                    Log.w("Chattings.makeRoom()", "채팅방 생성 실패(cancel)")
                    continuation.resume(false)
                }
        }
        return result
    }

    //채팅방 생성
    suspend fun joinRoom(roomID: String): Boolean {
        /*TODO 채팅방 상태 available 인지 확인하기*/
        val chattingRoomMap = mapOf(
            "chattings/chats/$roomID/members/${Firebase.auth.currentUser?.uid}" to true,
        )
        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(chattingRoomMap)
                .addOnSuccessListener {
                    Log.w("Chattings.joinRoom()", "채팅방 참가 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Chattings.joinRoom()", "채팅방 참가 실패(fail): $it")
                    continuation.resume(false)
                }
                .addOnCanceledListener {
                    Log.w("Chattings.joinRoom()", "채팅방 참가 실패(cancel)")
                    continuation.resume(false)
                }
        }
        return result
    }

    //채팅방 나가기
    suspend fun leaveRoom(roomID: String): Boolean {
        /*TODO 채팅방 참가중인지 확인하기*/
        val chattingRoomMap = mapOf(
            "chattings/chats/$roomID/members/${Firebase.auth.currentUser?.uid}" to true,
        )
        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(chattingRoomMap)
                .addOnSuccessListener {
                    Log.w("Chattings.leaveRoom()", "채팅방 나가기 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Chattings.leaveRoom()", "채팅방 나가기 실패(fail): $it")
                    continuation.resume(false)
                }
                .addOnCanceledListener {
                    Log.w("Chattings.leaveRoom()", "채팅방 나가기 실패(cancel)")
                    continuation.resume(false)
                }
        }
        return result
    }

    //채팅 보내기
    fun sendChat() {

    }

    //채팅방 삭제하기
    suspend fun deleteRoom(roomID: String): Boolean {
        /*TODO 채팅방 방장인지, 상태 available 인지 확인하기*/
        val chattingRoomMap = mapOf(
            "chattings/chats/$roomID/info/state" to "deleted",
        )
        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(chattingRoomMap)
                .addOnSuccessListener {
                    Log.w("Chattings.deleteRoom()", "채팅방 삭제 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Chattings.deleteRoom()", "채팅방 삭제 실패(fail): $it")
                    continuation.resume(false)
                }
                .addOnCanceledListener {
                    Log.w("Chattings.deleteRoom()", "채팅방 삭제 실패(cancel)")
                    continuation.resume(false)
                }
        }
        return result
    }

    //채팅방 설정하기
    fun setRoom() {

    }

}