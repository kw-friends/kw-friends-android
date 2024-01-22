package hello.kwfriends.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hello.kwfriends.ui.component.EventCard
import hello.kwfriends.ui.component.ParticipatedGatheringListCard
import hello.kwfriends.ui.screens.main.MainViewModel

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        EventCard(5)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFFFFBFF))
                .padding(horizontal = 14.dp)
        ) {
            ParticipatedGatheringListCard(
                participatedGatherings = mainViewModel.participatedGatherings,
                mainViewModel = mainViewModel
            )
        }
    }
}