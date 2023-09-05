package hello.kwfriends.ui.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String, val icon: ImageVector, val screenRoute: String
) {
    object MyPage : BottomNavItem(
        title = "마이페이지",
        icon = Icons.Default.AccountCircle,
        screenRoute = "마이페이지"
    )

    object FindGathering : BottomNavItem(
        title = "모임 찾기",
        icon = Icons.Default.Group,
        screenRoute = "모임 찾기"
    )

    object Settings : BottomNavItem(
        title = "설정",
        icon = Icons.Default.Settings,
        screenRoute = "설정"
    )
}