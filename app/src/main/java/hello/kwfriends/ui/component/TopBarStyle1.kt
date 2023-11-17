package hello.kwfriends.ui.component

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import hello.kwfriends.ui.main.Routes

@OptIn(ExperimentalMaterial3Api::class) // Ongoing
@Composable
fun TopBarStyle1(
    title: String,
    containerColor: Color,
    navigation: NavController,

) {
    TopAppBar(
        title = { Text(text = title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor
        ),
        actions = {
            IconButton(onClick = { navigation.navigate(Routes.SETTINGS_SCREEN) }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Account"
                )
            }
        }
    )
}