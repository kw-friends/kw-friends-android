package hello.kwfriends.ui.screens.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NewPostViewModel : ViewModel() {
    var gatheringTitle by mutableStateOf<String>("")
    var gatheringPromoter by mutableStateOf<String>("")
    var gatheringTime by mutableStateOf<String>("")
    var gatheringLocation by mutableStateOf<String>("")
    var maximumMemberCount by mutableIntStateOf(0)


    fun gatheringTitleChange(text: String) {gatheringTitle = text}
    fun gatheringTimeChange(text: String) {gatheringTime = text}
    fun gatheringLocationChange(text: String) {gatheringLocation = text}
    fun maximumMemberCountChange(text: Int) {maximumMemberCount = text}


}