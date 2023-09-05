package hello.kwfriends.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.ui.component.NavigationBar
import hello.kwfriends.ui.component.NavigationGraph
import hello.kwfriends.ui.component.ToolBarWithTitle


@Composable
fun MainScreenView() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { ToolBarWithTitle(navController = navController) },
        bottomBar = { NavigationBar(navController = navController) }
    ) {
        Box(modifier = Modifier.padding(it)) {
            NavigationGraph(navController = navController)
        }
    }
}
