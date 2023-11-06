package hello.kwfriends.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import hello.kwfriends.ui.screens.findGathering.FindGatheringCardList
import hello.kwfriends.ui.screens.post.NewPostScreen
import hello.kwfriends.ui.screens.post.NewPostViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    when (viewModel.uiState) {
        MainUiState.Home -> {
            /*val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route*/
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "KW Friends") },
                        backgroundColor = Color(0xFFE2A39B),
                        actions = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Account"
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "모임 생성하기") },
                        icon = { Icon(Icons.Default.Add, null) },
                        onClick = {
                            viewModel.goToNewPostPage()
                            Toast.makeText(context, "모임 글 생성 페이지 진입", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.padding(bottom = 35.dp)
                    )
                }
            ) {
                Box(modifier = Modifier.padding(it)) {
                    FindGatheringCardList(viewModel = MainViewModel())
                    /*NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.FindGathering.screenRoute
                    ) {
                        composable(BottomNavItem.FindGathering.screenRoute) {
                            FindGatheringCardList(viewModel = MainViewModel())
                        }
                        composable(BottomNavItem.MyPage.screenRoute) {
                            MyPageScreen()
                        }
                        composable(BottomNavItem.Settings.screenRoute) {
                            SettingsScreen()
                        }
                    }*/
                }
            }
        }

        MainUiState.NewPost -> {
            NewPostScreen(mainViewModel = viewModel, postViewModel = NewPostViewModel())
        }
    }
}