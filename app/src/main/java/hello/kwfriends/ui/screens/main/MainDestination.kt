package hello.kwfriends.ui.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector
import hello.kwfriends.R

enum class MainDestination(
    val route: String,
    val icon: ImageVector,
    val labelResId: Int
) {
    HomeScreen(
        route = ROUTE_HOME,
        icon = Icons.Default.Home,
        labelResId = R.string.navigation_home
    ),
    FindGatheringScreen(
        route = ROUTE_FIND_GATHERING,
        icon = Icons.Default.People,
        labelResId = R.string.navigation_findGathering
    ),
    ChatScreen(
        route = ROUTE_CHAT,
        icon = Icons.Default.Chat,
        labelResId = R.string.navigation_chat
    )
}

private const val ROUTE_HOME = "home"
private const val ROUTE_FIND_GATHERING = "findGathering"
private const val ROUTE_CHAT = "chat"