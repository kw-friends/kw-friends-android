package hello.kwfriends.ui.screens.main

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.ui.screens.findGathering.findGatheringScreen
import hello.kwfriends.ui.screens.myPage.MyPageScreen
import hello.kwfriends.ui.screens.settings.SettingsScreen


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { ToolBarWithTitle(navController = navController) },
        bottomBar = { NavigationBar(navController = navController) }
    ) {
        Box(modifier = Modifier.padding(it)) {
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
    }
}

@Composable //상단 바
fun ToolBarWithTitle(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    TopAppBar(
        title = {
            if (currentRoute != null) {
                Text(text = currentRoute, fontSize = 22.sp)
            }
        },
        backgroundColor = Color(0xFFE4C5C5)
    )
}

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