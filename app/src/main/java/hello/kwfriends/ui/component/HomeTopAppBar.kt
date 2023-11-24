package hello.kwfriends.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hello.kwfriends.ui.main.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    navigation: NavController,
    isSearching: Boolean,
    clickSearchButton: () -> Unit,
    clickBackButton: () -> Unit
    ) {
    TopAppBar(
        title = {
            AnimatedVisibility(
                visible = !isSearching,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(durationMillis = 100))
            ) {
                Text(
                    text = "내 모임",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(5.dp),
                    maxLines = 1
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFE2A39B)
        ),
        actions = {
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (isSearching) {
                        IconButton(
                            onClick = clickBackButton,
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Exit",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    SearchTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier
                            .animateContentSize(animationSpec = tween(easing = LinearOutSlowInEasing))
                            .width(if(isSearching) 290.dp else 0.dp)
                    )
                }
                IconButton(
                    onClick = clickSearchButton,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.CenterEnd)

                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(35.dp)
                    )
                }
            }

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
}