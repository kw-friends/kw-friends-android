package hello.kwfriends.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavItem(
    val name: String,
    val icon: ImageVector,
    val route: String,
)


@Composable
fun BottomNavigationIcon(
    onView: MainUiState,
    icon: ImageVector,
    iconTarget: MainUiState,
    iconTitle: String,
    onClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(width = 115.dp, height = 75.dp)
            .background(if (onView == iconTarget) Color(0xFFD56450) else Color(0xFFFFD9C9), shape = RoundedCornerShape(14.dp))
            .clickable(onClick = onClicked)

    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            Modifier.size(32.dp)
        )
        Text(text = iconTitle, fontSize = 14.sp)
    }
}

@Composable
fun BottomNavigationBar(
    viewModel: MainViewModel
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFFFD9C9))
            .padding(5.dp)
    ) {
        BottomNavigationIcon(
            onView = viewModel.uiState,
            icon = Icons.Default.AccountCircle,
            iconTarget = MainUiState.MyPage,
            iconTitle = "마이페이지",
            onClicked = { viewModel.onClickedMyPage() }
        )
        BottomNavigationIcon(
            onView = viewModel.uiState,
            icon = Icons.Default.Group,
            iconTarget = MainUiState.Home,
            iconTitle = "모임 찾기",
            onClicked = { viewModel.onClickedHome() }
        )
        BottomNavigationIcon(
            onView = viewModel.uiState,
            icon = Icons.Default.Settings,
            iconTarget = MainUiState.Settings,
            iconTitle = "설정",
            onClicked = { viewModel.onClickedSettings() }
        )
    }
}