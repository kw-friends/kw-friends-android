package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ServerValue
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class MessageType {
    TEXT,
    IMAGE,
    AUDIO,
    VIDEO
}

enum class ChattingRoomType {
    GROUP,
    DIRECT
}

enum class ChattingRoomState {
    AVAILABLE,
    DELETED
}

object Chattings {
    private var database = Firebase.database.reference

    var chattingRoomDatas by mutableStateOf<Map<String, Any>?>(mutableMapOf())

    //채팅방 만들기
    suspend fun make(title: String, type: ChattingRoomType, owners: List<String>, members: List<String>): String? {
        val roomID = database.child("chattings").child("rooms").push().key
        val chattingRoomMap = mutableMapOf<String, Any>(
            "chattings/rooms/$roomID/title" to title,
            "chattings/rooms/$roomID/state" to ChattingRoomState.AVAILABLE,
            "chattings/rooms/$roomID/type" to type
        )
        owners.forEach {
            chattingRoomMap["chattings/rooms/$roomID/owner/$it"] = true
        }
        members.forEach {
            chattingRoomMap["chattings/rooms/$roomID/members/$it"] = true
        }
        val result = suspendCoroutine<String?> { continuation ->
            database.updateChildren(chattingRoomMap)
                .addOnSuccessListener {
                    Log.w("Chattings.makeRoom()", "채팅방 생성 성공: $roomID")
                    continuation.resume(roomID)
                }
                .addOnFailureListener {
                    Log.w("Chattings.makeRoom()", "채팅방 생성 실패(fail): $it")
                    continuation.resume(null)
                }
                .addOnCanceledListener {
                    Log.w("Chattings.makeRoom()", "채팅방 생성 실패(cancel)")
                    continuation.resume(null)
                }
        }
        return result
    }

    //채팅방 생성
    suspend fun join(roomID: String): Boolean {
        val info = getRoomInfo(roomID)
        if(info?.get("state") != ChattingRoomState.AVAILABLE) {
            Log.w("Chattings.join()", "채팅방 상태가 available이 아니라 채팅방 참가에 실패했습니다. 상태: ${info?.get("state")}")
            return false
        }
        val chattingRoomMap = mapOf(
            "chattings/rooms/$roomID/members/${Firebase.auth.currentUser?.uid}" to true,
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
    suspend fun leave(roomID: String): Boolean {
        val info = getRoomInfo(roomID)
        val members = info?.get("members") as Map<String, Boolean>
        if(Firebase.auth.currentUser!!.uid !in members) {
            Log.w("Chattings.leave()", "채팅방에 참여중이 아니라 채팅방 나가기에 실패했습니다.")
            return false
        }
        val chattingRoomMap = mapOf(
            "chattings/chats/$roomID/members/${Firebase.auth.currentUser?.uid}" to false,
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
    suspend fun sendMessage(roomID: String, uid: String, content: String, type: MessageType): Boolean {
        val info = getRoomInfo(roomID)
        val members = info?.get("members") as Map<String, Boolean>
        if(info["state"] != ChattingRoomState.AVAILABLE) {
            Log.w("Chattings.sendMessage()", "채팅방 상태가 available이 아니라 채팅방 전송에 실패했습니다. 상태: ${info["state"]}")
            return false
        }
        else if(Firebase.auth.currentUser!!.uid !in members) {
            Log.w("Chattings.leave()", "채팅방에 참여중이 아니라 채팅방 나가기에 실패했습니다.")
            return false
        }
        val messageID = database.child("chattings").child("messages").push().key
        val chattingRoomMap = mutableMapOf<String, Any>(
            "chattings/messages/$roomID/$messageID/uid" to uid,
            "chattings/messages/$roomID/$messageID/content" to content,
            "chattings/messages/$roomID/$messageID/type" to type,
            "chattings/messages/$roomID/$messageID/timestamp" to ServerValue.TIMESTAMP,
            "chattings/rooms/$roomID/recentMessage/timestamp" to ServerValue.TIMESTAMP,
        )
        if(type == MessageType.TEXT) chattingRoomMap["chattings/rooms/$roomID/recentMessage/content"] = content
        else if(type == MessageType.IMAGE) chattingRoomMap["chattings/rooms/$roomID/recentMessage/content"] = "(사진)"
        else if(type == MessageType.AUDIO) chattingRoomMap["chattings/rooms/$roomID/recentMessage/content"] = "(음성녹음)"
        else if(type == MessageType.VIDEO) chattingRoomMap["chattings/rooms/$roomID/recentMessage/content"] = "(영상)"
        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(chattingRoomMap)
                .addOnSuccessListener {
                    Log.w("Chattings.sendMessage()", "메세지 전송 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Chattings.sendMessage()", "메세지 전송 실패(fail): $it")
                    continuation.resume(false)
                }
                .addOnCanceledListener {
                    Log.w("Chattings.sendMessage()", "메세지 전송 실패(cancel)")
                    continuation.resume(false)
                }
        }
        return result
    }

    //채팅방 삭제하기
    suspend fun delete(roomID: String): Boolean {
        val info = getRoomInfo(roomID)
        val owners = info?.get("owners") as Map<String, Boolean>
        if(info["state"] != ChattingRoomState.AVAILABLE) {
            Log.w("Chattings.delete()", "채팅방 상태가 available이 아니라 채팅방 삭제 실패했습니다. 상태: ${info["state"]}")
            return false
        }
        else if(Firebase.auth.currentUser!!.uid !in owners) {
            Log.w("Chattings.delete()", "채팅방 주인이 아니라 채팅방 삭제에 실패했습니다.")
            return false
        }
        val chattingRoomMap = mapOf(
            "chattings/chats/$roomID/info/state" to ChattingRoomState.DELETED,
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

    //채팅방 목록 및 정보 가져오기
    suspend fun getRoomList(): Boolean {
        val result = suspendCoroutine<Boolean> { continuation ->
            database.child("chattings").child("rooms").get()
                .addOnSuccessListener { dataSnapshot ->
                    val data = dataSnapshot.getValue<MutableMap<String, MutableMap<String, Any>>>()
                    data?.forEach {
                        data[it.key]?.set("state", ChattingRoomState.valueOf(it.value["state"].toString()))
                        data[it.key]?.set("type", ChattingRoomType.valueOf(it.value["type"].toString()))
                    }
                    chattingRoomDatas = data
                    Log.w("Chattings.getRoomList()", "데이터 가져오기 성공 $data")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Chattings.getRoomList()", "데이터 가져오기 실패: $it")
                    continuation.resume(false)
                }
                .addOnCanceledListener {
                    Log.w("Chattings.getRoomList()", "데이터 가져오기 캔슬")
                    continuation.resume(false)
                }
        }
        return result
    }

    //채팅방 정보 한번만 가져와서 반환하는 함수
    suspend fun getRoomInfo(roomID: String): Map<String, Any>?{
        val result = suspendCoroutine<Map<String, Any>?> { continuation ->
            database.child("chattings").child("rooms").child(roomID).get()
                .addOnSuccessListener { dataSnapshot ->
                    val data = dataSnapshot.getValue<Map<String, Any>>()?.toMutableMap()
                    if(data != null) {
                        data["state"] = ChattingRoomState.valueOf(data["state"].toString())
                        data["type"] = ChattingRoomType.valueOf(data["type"].toString())
                    }
                    Log.w("Chattings.getRoomInfo()", "데이터 가져오기 성공 $data")
                    continuation.resume(data)
                }
                .addOnFailureListener {
                    Log.w("Chattings.getRoomInfo()", "데이터 가져오기 실패")
                    continuation.resume(null)
                }
                .addOnCanceledListener {
                    Log.w("Chattings.getRoomInfo()", "데이터 가져오기 캔슬")
                    continuation.resume(null)
                }
        }
        return result
    }

    //채팅방 메세지 한번만 가져와서 반환하는 함수
    suspend fun getRoomMessages(roomID: String): Map<String, Any>?{
        val result = suspendCoroutine<Map<String, Any>?> { continuation ->
            database.child("chattings").child("messages").child(roomID).get()
                .addOnSuccessListener { dataSnapshot ->
                    val data = dataSnapshot.getValue<Map<String, Any>>()?.toMutableMap()
                    if(data != null) {
                        data["type"] = MessageType.valueOf(data["type"].toString())
                    }
                    Log.w("Chattings.getRoomMessages()", "데이터 가져오기 성공 $data")
                    continuation.resume(data)
                }
                .addOnFailureListener {
                    Log.w("Chattings.getRoomMessages()", "데이터 가져오기 실패")
                    continuation.resume(null)
                }
                .addOnCanceledListener {
                    Log.w("Chattings.getRoomMessages()", "데이터 가져오기 캔슬")
                    continuation.resume(null)
                }
        }
        return result
    }

    //채팅방 정보 수정하기
    fun setRoom() {

    }

}