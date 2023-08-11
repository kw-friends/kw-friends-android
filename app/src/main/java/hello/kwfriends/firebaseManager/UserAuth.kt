package hello.kwfriends.firebaseManager

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object UserAuth {

    private val fa = Firebase.auth

    //로그인 함수
    suspend fun signIn(email: String, password: String): Boolean {
        if (email == "" || password == "") {
            Log.w("Lim", "이메일 또는 비밀번호를 입력하지 않았습니다.")
            return false
        }
        val result = suspendCoroutine<Boolean> { continuation ->
            fa.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.w("Lim", "로그인 시도 성공")
                        if (Firebase.auth.currentUser?.isEmailVerified!!) {
                            Log.w("Lim", "로그인 성공!")
                            continuation.resume(true)
                        } else {
                            Log.w("Lim", "로그인 실패")
                            continuation.resume(false)
                        }
                    } else {
                        Log.w("Lim", "로그인 시도 실패")
                        continuation.resume(false)
                    }
                }
        }
        return result
    }

    //로그아웃 함수
    fun signOut() {
        fa.signOut()
        Log.w("Lim", "로그아웃")
    }

    //유저 생성(이메일, 비밀번호 firebase auth에 등록)
    suspend fun createUser(email: String, password: String): Boolean{
        val result = suspendCoroutine<Boolean> { continuation ->
            fa.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) { //이메일 등록 성공
                        Log.w("Lim", "이메일 등록에 성공했습니다.")
                        continuation.resume(true)
                    } else {
                        Log.w("Lim", "이메일 등록에 실패했습니다.")
                        continuation.resume(false)

                    }
                }
        }
        return result
    }

    //회원탈퇴
    suspend fun deleteUser(): Boolean{
        val result = suspendCoroutine<Boolean> { continuation ->
            Firebase.auth.currentUser?.delete()
                ?.addOnSuccessListener {
                    Log.w("Lim", "성공적으로 계정을 삭제했습니다.")
                    continuation.resume(true)
                }
                ?.addOnFailureListener {
                    Log.w("Lim", "계정을 삭제하는데 실패했습니다. error=${it}")
                    continuation.resume(false)
                }
        }
        return result
    }

    //이메일 전송
    suspend fun sendAuthEmail(): Boolean{
        val result = suspendCoroutine<Boolean> { continuation ->
            fa.currentUser?.sendEmailVerification()
                ?.addOnSuccessListener {
                    Log.w("Lim", "인증 메일 전송에 성공했습니다.")
                    continuation.resume(true)
                }
                ?.addOnFailureListener {
                    Log.w("Lim", "인증 메일 전송에 실패했습니다.", it)
                    continuation.resume(false)
                }
        }
        return result
    }

    //유저 인증 정보 리로드 (firebase auth reload)
    suspend fun reload(): Boolean{
        val result = suspendCoroutine<Boolean> { continuation ->
            fa.currentUser!!.reload()
                .addOnSuccessListener {
                    Log.w("Lim", "유저 인증 상태 리로드 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Lim", "유저 인증 상태 리로드 실패")
                    continuation.resume(true)
                }
        }
        return result
    }

    //사용자 재인증 함수
    suspend fun reAuth(email: String, password: String): Boolean {
        val credential = EmailAuthProvider.getCredential(email, password)
        val result = suspendCoroutine<Boolean> { continuation ->
            fa.currentUser!!.reauthenticate(credential)
                .addOnSuccessListener {
                    Log.w("Lim", "재인증 성공")
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    Log.w("Lim", "재인증 실패", it)
                    continuation.resume(false)
                }
        }
        return result
    }
}