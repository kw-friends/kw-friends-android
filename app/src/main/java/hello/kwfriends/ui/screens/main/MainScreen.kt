package hello.kwfriends.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.ui.screens.findGathering.findGatheringScreen
import hello.kwfriends.ui.screens.myPage.MyPageScreen
import hello.kwfriends.ui.screens.post.NewPostScreen
import hello.kwfriends.ui.screens.post.NewPostViewModel
import hello.kwfriends.ui.screens.settings.SettingsScreen


@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    when (viewModel.uiState){
        MainUiState.Home -> {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            Scaffold(
                topBar = {
                    if (currentRoute != null) {
                        ToolBarWithTitle(currentRoute)
                    }
                },
                bottomBar = { NavigationBar(navController = navController) },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "모임 생성하기") },
                        icon = { Icon(Icons.Default.Add, null)},
                        onClick = {
                            viewModel.goToNewPostPage()
                            Toast.makeText(context, "모임 글 생성 페이지 진입", Toast.LENGTH_SHORT).show()

                            /*CoroutineScope(Dispatchers.IO).launch {
                                PostManager.uploadPost(
                                    gatheringTitle = "Sample Title",
                                    gatheringLocation = "Sample Location",
                                    gatheringPromoter = "Someone",
                                    gatheringTime = "Someday",
                                    maximumParticipant = 10
                                )
                            }*/
                        })
                }
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

        MainUiState.NewPost -> {
            NewPostScreen(mainViewModel = viewModel, postViewModel = NewPostViewModel())
        }
    }


}

@Composable //상단 바
fun ToolBarWithTitle(
    text: String
) {


    TopAppBar(
        title = {
            Text(text = text, fontSize = 22.sp)
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