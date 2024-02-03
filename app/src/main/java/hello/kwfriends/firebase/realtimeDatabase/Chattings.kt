package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class RoomDetail(
    var roomID: String = "",
    var title: String = "",
    var state: ChattingRoomState = ChattingRoomState.DELETED,
    var owners: Map<String, Any> = emptyMap(),
    var members: Map<String, Any> = emptyMap(),
    var type: ChattingRoomType = ChattingRoomType.DIRECT,
    var recentMessage: RecentMessage = RecentMessage()
)

data class MessageDetail(
    var messageID: String = "",
    var uid: String = "",
    var content: String = "",
    var type: MessageType = MessageType.TEXT,
    var timestamp: Any = "",
    var read: Map<String, Any> = emptyMap()
)

data class RecentMessage(
    var content: String = "",
    var timestamp: Any = ""
)

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
    val database = Firebase.database.reference

    var chattingRoomList by mutableStateOf<MutableMap<String, RoomDetail>?>(mutableMapOf())

    var messageListenerCount: MutableList<Pair<String, ChildEventListener>> by mutableStateOf(mutableListOf())
    var roomListListenerCount: MutableList<ChildEventListener> by mutableStateOf(mutableListOf())

    //채팅방 들어갔을 때 메세지 리스너
    fun addMessageListener(roomID: String, update: (MessageDetail) -> Unit) {
        val reference = database.child("chattings").child("messages").child(roomID)
        val chattingListener = object : ChildEventListener {
            var messageDetail: MessageDetail? = null
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                messageDetail = dataSnapshot.getValue<MessageDetail>() ?: return
                if(messageDetail != null) update(messageDetail!!)
                Log.w("messageListener.onChildAdded", "onChildAdded: $messageDetail")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                messageDetail = dataSnapshot.getValue<MessageDetail>() ?: return
                Log.w("messageListener.onChildChanged", "onChildChanged: $messageDetail")
                if(messageDetail != null) update(messageDetail!!)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                messageDetail = dataSnapshot.getValue<MessageDetail>() ?: return
                Log.w("messageListener.onChildRemoved", "onChildRemoved: $messageDetail")
                if(messageDetail != null) update(messageDetail!!)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                messageDetail = dataSnapshot.getValue<MessageDetail>() ?: return
                Log.w("messageListener.onChildMoved", "onChildMoved: $messageDetail")
                if(messageDetail != null) update(messageDetail!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(
                    "messageListener.onCancelled",
                    "onCancelled: ",
                    databaseError.toException()
                )
            }
        }

        messageListenerCount.add(roomID to chattingListener)
        reference.addChildEventListener(chattingListener)
        Log.d("addMessageListener", "${roomID}에 messageListener 추가")
    }

    //채팅방 나갈 때 메세지 리스너 제거
    fun removeMessageListener() {
        messageListenerCount.forEach {
            val reference = database.child("chattings").child("messages").child(it.first)
            reference.removeEventListener(it.second)
            Log.d("removeMessageListener", "${it.first}에서 messageListener 제거")
        }
        messageListenerCount = mutableListOf()
    }

    //채팅방 목록 가져오는 리스너 추가
    fun addRoomListListener() {
        val reference = database.child("chattings").child("rooms")
        val chattingListener = object : ChildEventListener {
            var roomDetail: RoomDetail? = null
            fun update(roomDetail: RoomDetail) {
                chattingRoomList = chattingRoomList?.toMutableMap().apply {
                    this?.set(roomDetail.roomID, roomDetail)
                }
            }
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                roomDetail = dataSnapshot.getValue<RoomDetail>() ?: return
                if(roomDetail != null) update(roomDetail!!)
                Log.w("roomListener.onChildAdded", "onChildAdded: $roomDetail")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                roomDetail = dataSnapshot.getValue<RoomDetail>() ?: return
                Log.w("roomListener.onChildChanged", "onChildChanged: $roomDetail")
                if(roomDetail != null) update(roomDetail!!)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                roomDetail = dataSnapshot.getValue<RoomDetail>() ?: return
                Log.w("roomListener.onChildRemoved", "onChildRemoved: $roomDetail")
                chattingRoomList = chattingRoomList?.toMutableMap().apply {
                    this?.remove(roomDetail?.roomID)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                roomDetail = dataSnapshot.getValue<RoomDetail>() ?: return
                Log.w("roomListener.onChildMoved", "onChildMoved: $roomDetail")
                if(roomDetail != null) update(roomDetail!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(
                    "roomListener.onCancelled",
                    "onCancelled: ",
                    databaseError.toException()
                )
            }
        }

        roomListListenerCount.add(chattingListener)
        reference.addChildEventListener(chattingListener)
        Log.d("addRoomListener", "roomListener 추가")
    }

    //채팅방 목록 가져오는 리스너 제거
    fun removeRoomListListener() {
        roomListListenerCount.forEach {
            val reference = database.child("chattings").child("rooms")
            reference.removeEventListener(it)
            Log.d("removeRoomListListener", "roomListener 제거")
        }
        roomListListenerCount = mutableListOf()
    }

    //채팅방 만들기
    suspend fun make(
        title: String,
        type: ChattingRoomType,
        owners: List<String>,
        members: List<String>
    ): String? {
        val roomID = database.child("chattings").child("rooms").push().key
        val chattingRoomMap = mutableMapOf<String, Any>(
            "chattings/rooms/$roomID/roomID" to roomID.toString(),
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
        if (info?.state != ChattingRoomState.AVAILABLE) {
            Log.w(
                "Chattings.join()",
                "채팅방 상태가 available이 아니라 채팅방 참가에 실패했습니다. 상태: ${info?.state}"
            )
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
        val members = info?.members
        if (members?.contains(Firebase.auth.currentUser!!.uid) != true) {
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
    suspend fun sendMessage(
        roomID: String,
        uid: String,
        content: String,
        type: MessageType
    ): Boolean {
        val info = getRoomInfo(roomID)
        val members = info?.members
        if (info?.state != ChattingRoomState.AVAILABLE) {
            Log.w(
                "Chattings.sendMessage()",
                "채팅방 상태가 available이 아니라 채팅방 전송에 실패했습니다. 상태: ${info?.state}"
            )
            return false
        } else if (members?.contains(Firebase.auth.currentUser!!.uid) != true) {
            Log.w("Chattings.leave()", "채팅방에 참여중이 아니라 채팅방 나가기에 실패했습니다.")
            return false
        }
        val messageID = database.child("chattings").child("messages").push().key
        val chattingRoomMap = mutableMapOf<String, Any>(
            "chattings/messages/$roomID/$messageID/uid" to uid,
            "chattings/messages/$roomID/$messageID/content" to content,
            "chattings/messages/$roomID/$messageID/type" to type,
            "chattings/messages/$roomID/$messageID/read/$uid" to ServerValue.TIMESTAMP,
            "chattings/messages/$roomID/$messageID/messageID" to messageID.toString(),
            "chattings/messages/$roomID/$messageID/timestamp" to ServerValue.TIMESTAMP,
            "chattings/rooms/$roomID/recentMessage/timestamp" to ServerValue.TIMESTAMP,
        )
        when (type) {
            MessageType.TEXT -> chattingRoomMap["chattings/rooms/$roomID/recentMessage/content"] =
                content
            MessageType.IMAGE -> chattingRoomMap["chattings/rooms/$roomID/recentMessage/content"] =
                "(사진)"
            MessageType.AUDIO -> chattingRoomMap["chattings/rooms/$roomID/recentMessage/content"] =
                "(음성녹음)"
            MessageType.VIDEO -> chattingRoomMap["chattings/rooms/$roomID/recentMessage/content"] =
                "(영상)"
        }
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
        val owners = info?.owners
        if (info?.state != ChattingRoomState.AVAILABLE) {
            Log.w(
                "Chattings.delete()",
                "채팅방 상태가 available이 아니라 채팅방 삭제 실패했습니다. 상태: ${info?.state}"
            )
            return false
        } else if (owners?.contains(Firebase.auth.currentUser!!.uid) != true) {
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
        val uid = Firebase.auth.currentUser!!.uid
        val result = suspendCoroutine<Boolean> { continuation ->
            database.child("chattings").child("rooms").get()
                .addOnSuccessListener { dataSnapshot ->
                    var data = dataSnapshot.getValue<MutableMap<String, RoomDetail>>()
                    data = data?.filter { it.value.members.containsKey(uid) }?.toMutableMap()
                    data?.forEach {
                        data[it.key]?.state = ChattingRoomState.valueOf(it.value.state.toString())
                        data[it.key]?.type = ChattingRoomType.valueOf(it.value.type.toString())
                    }
                    chattingRoomList = data
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
    suspend fun getRoomInfo(roomID: String): RoomDetail? {
        val result = suspendCoroutine<RoomDetail?> { continuation ->
            database.child("chattings").child("rooms").child(roomID).get()
                .addOnSuccessListener { dataSnapshot ->
                    val data = dataSnapshot.getValue<RoomDetail>()
                    if (data != null) {
                        data.state = ChattingRoomState.valueOf(data.state.toString())
                        data.type = ChattingRoomType.valueOf(data.type.toString())
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
    suspend fun getRoomMessages(roomID: String): MutableMap<String, MessageDetail>? {
        val result = suspendCoroutine<MutableMap<String, MessageDetail>?> { continuation ->
            database.child("chattings").child("messages").child(roomID).orderByChild("timestamp")
                .get()
                .addOnSuccessListener { dataSnapshot ->
                    val data = dataSnapshot.getValue<MutableMap<String, MessageDetail>>()
                    data?.forEach {
                        data[it.key]?.type = MessageType.valueOf(data[it.key]?.type.toString())
                    }
                    Log.w("Chattings.getRoomMessages()", "데이터 가져오기 성공 $data")
                    continuation.resume(data)
                }
                .addOnFailureListener {
                    Log.w("Chattings.getRoomMessages()", "데이터 가져오기 실패: $it")
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

    //메세지 읽음처리
    suspend fun messageRead(roomID: String, messageData: MutableMap<String, MessageDetail>): Boolean {
        val uid = Firebase.auth.currentUser!!.uid
        val readMap = mutableMapOf<String, Any>()
        messageData.forEach {
            if(it.value.read[uid] == null) {
                readMap["chattings/messages/$roomID/${it.key}/read/$uid"] = ServerValue.TIMESTAMP
            }
        }
        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(readMap)
                .addOnSuccessListener {
                    Log.w("Chattings.messageRead()", "$roomID 채팅방 메세지 읽음처리 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Chattings.messageRead()", "$roomID 채팅방 메세지 읽음처리 실패(fail): $it")
                    continuation.resume(false)
                }
                .addOnCanceledListener {
                    Log.w("Chattings.messageRead()", "$roomID 채팅방 메세지 읽음처리 실패(cancel)")
                    continuation.resume(false)
                }
        }
        return result
    }

    suspend fun makeDirectChatting(targetUid: String): String {
        getRoomList()
        var roomID: String = ""
        var already = false
        val uid = Firebase.auth.currentUser!!.uid
        chattingRoomList?.forEach {
            if(it.value.type == ChattingRoomType.DIRECT) {
                if((targetUid in it.value.members) && (uid in it.value.members)) {
                    roomID = it.key
                    already = true
                }
            }
        }
        if(!already) {
            roomID = make(
                title = "개인 채팅",
                type = ChattingRoomType.DIRECT,
                owners = listOf(uid, targetUid),
                members = listOf(uid, targetUid),
            )?:""
            if (roomID != null) {
                sendMessage(
                    roomID = roomID,
                    uid = "BROADCAST",
                    content = "개인 채팅방이 생성되었습니다",
                    type = MessageType.TEXT
                )
            }
        }
        return roomID
    }

}