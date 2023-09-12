package hello.kwfriends.ui.screens.main

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.ui.component.NavigationBar
import hello.kwfriends.ui.component.ToolBarWithTitle
import hello.kwfriends.ui.screens.findGathering.findGatheringScreen
import hello.kwfriends.ui.screens.myPage.MyPageScreen
import hello.kwfriends.ui.screens.settings.SettingsScreen


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backDisPatcher = LocalOnBackPressedDispatcherOwner.current
    val currentRoute = remember {
        navController.currentBackStackEntry?.destination?.route
    }
    val backCallback = BackHandler {

    }
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
