package hello.kwfriends.ui.screens.main

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import hello.kwfriends.ui.screens.findGathering.findGatheringScreen
import hello.kwfriends.ui.screens.myPage.MyPageScreen
import hello.kwfriends.ui.screens.settings.SettingsScreen

@Composable //상단 바
fun ToolBarWithTitle(title: String, modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(text = title, fontSize = 22.sp) },
        backgroundColor = Color(0xFFE4C5C5),
        modifier = modifier
    )
}


@Composable
fun NavigationBar(navController: NavHostController) {
    val items = listOf<BottomNavItem>(
        BottomNavItem.myPage,
        BottomNavItem.findGathering,
        BottomNavItem.settings
    )


    BottomNavigation(
        backgroundColor = Color(0xFFFFD9C9),
        contentColor = Color(0xFFD56450)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        for (item in items) {
            BottomNavigationItem(
                selected = currentRoute == item.screenRoute,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = { Text(text = item.title) },
                alwaysShowLabel = true,
                selectedContentColor = Color(0xFFD56450),
                unselectedContentColor = Color(0xFF929292)
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.findGathering.screenRoute
    ) {
        composable(BottomNavItem.findGathering.screenRoute) {
            findGatheringScreen()
        }
        composable(BottomNavItem.myPage.screenRoute) {
            MyPageScreen()
        }
        composable(BottomNavItem.settings.screenRoute) {
            SettingsScreen()
        }
    }
}

sealed class BottomNavItem(
    val title: String, val icon: ImageVector, val screenRoute: String
) {
    object myPage : BottomNavItem(
        "마이페이지",
        Icons.Default.AccountCircle,
        "myPage"
    )

    object findGathering : BottomNavItem(
        "모임 찾기",
        Icons.Default.Group,
        "findGathering"
    )

    object settings : BottomNavItem(
        "설정",
        Icons.Default.Settings,
        "settings"
    )
}