package hello.kwfriends.ui.screens.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NewPostViewModel : ViewModel() {
    var gatheringTitle by mutableStateOf("")
    var gatheringPromoter by mutableStateOf("")
    var gatheringTime by mutableStateOf("")
    var gatheringLocation by mutableStateOf("")
    var maximumMemberCount by mutableIntStateOf(0)
    var gatheringDescription by mutableStateOf("")


    fun gatheringTitleChange(text: String) {gatheringTitle = text}
    fun gatheringTimeChange(text: String) {gatheringTime = text}
    fun gatheringLocationChange(text: String) {gatheringLocation = text}
    fun gatheringDescriptionChange(text: String) {gatheringDescription = text}
    fun maximumMemberCountChange(text: Int) {maximumMemberCount = text}


}