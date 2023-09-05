package hello.kwfriends.ui.component

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

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