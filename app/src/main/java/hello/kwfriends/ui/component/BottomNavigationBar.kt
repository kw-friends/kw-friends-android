package hello.kwfriends.ui.component

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import hello.kwfriends.ui.screens.findGathering.findGatheringScreen
import hello.kwfriends.ui.screens.main.BottomNavItem
import hello.kwfriends.ui.screens.myPage.MyPageScreen
import hello.kwfriends.ui.screens.settings.SettingsScreen

@Composable
fun NavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.MyPage,
        BottomNavItem.FindGathering,
        BottomNavItem.Settings
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
                label = { Text(text = item.title, fontWeight = FontWeight(600)) },
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
        startDestination = BottomNavItem.FindGathering.screenRoute
    ) {
        composable(BottomNavItem.FindGathering.screenRoute) {
            findGatheringScreen()
        }
        composable(BottomNavItem.MyPage.screenRoute) {
            MyPageScreen()
        }
        composable(BottomNavItem.Settings.screenRoute) {
            SettingsScreen()
        }
    }
}