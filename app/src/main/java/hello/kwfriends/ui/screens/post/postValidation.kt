package hello.kwfriends.ui.screens.post

import android.util.Log

object postValidation {
    fun isStrHasData(text: String?): Boolean {
        Log.d("isStrHasData()", (text == null || text == "").toString())
        return !(text == null || text == "")
    }

    fun checkParticipantsRange(
        min: String,
        max: String
    ): Boolean { // min이 1이상, max가 2 이상, 100 이하이고, min < max인지를 확인
        return try {
            min.toInt() < max.toInt() && min.toInt() in 1..100 && max.toInt() in 2..100
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun checkHourRange(
        num: String
    ): Boolean {
        return try {
            num.toInt() in (0..23)
        } catch (e: NumberFormatException) {
            num == ""
        }
    }

    fun checkMinuteRange(
        num: String
    ): Boolean {
        return try {
            num.toInt() in (0..59)
        } catch (e: NumberFormatException) {
            num == ""
        }
    }
}