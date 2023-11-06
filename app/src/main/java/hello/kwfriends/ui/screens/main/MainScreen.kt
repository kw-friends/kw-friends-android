package hello.kwfriends.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hello.kwfriends.ui.screens.findGathering.FindGatheringCardList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, navigation: NavController) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "KW Friends",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE2A39B)
                ),
                actions = {
                    IconButton(
                        onClick = { navigation.navigate(Routes.SETTINGS_SCREEN) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "모임 생성하기") },
                icon = { Icon(Icons.Default.Add, null) },
                onClick = {
                    navigation.navigate(Routes.NEW_POST_SCREEN)
                },
                modifier = Modifier.padding(bottom = 35.dp)
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            FindGatheringCardList(viewModel = MainViewModel())
        }
    }
}