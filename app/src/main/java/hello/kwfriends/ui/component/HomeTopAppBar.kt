package hello.kwfriends.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.ui.main.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    navigation: NavController,
    isSearching: Boolean,
    searchText: String,
    setSearchText: (String) -> Unit,
    clickSearchButton: () -> Unit,
    clickBackButton: () -> Unit,
    focusRequester: FocusRequester
) {
    TopAppBar(
        modifier = Modifier
            .padding(top = 10.dp)
            .height(40.dp),
        title = {
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                AnimatedVisibility(
                    visible = !isSearching,
                    enter = fadeIn(),
                    exit = fadeOut(animationSpec = tween(durationMillis = 100))
                ) {
                    Text(
                        text = "모임 찾기",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Default,
                        maxLines = 1,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = Color(0xFFE2A39B)
        ),
        actions = {
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
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
                Box(if(isSearching) Modifier.weight(1f) else Modifier) {
                    Row(Modifier.align(Alignment.CenterEnd)) {
                        SearchTextField(
                            value = searchText,
                            onValueChange = setSearchText,
                            enable = isSearching,
                            modifier = Modifier
                                .animateContentSize(
                                    animationSpec = tween(easing = LinearOutSlowInEasing),

                                    )
                                .focusRequester(focusRequester)
                                .then(
                                    if (isSearching) Modifier.weight(1f) else Modifier.width(0.dp)
                                )

                        )
                    }
                    IconButton(
                        onClick = clickSearchButton,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(30.dp)
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
                        modifier = Modifier.size(30.dp)
                    )
                }
            }



        },
    )
}

@Preview
@Composable
fun HomeTopAppBarPreview() {
    val navController = rememberNavController()
    HomeTopAppBar(
        navigation = navController,
        isSearching = false,
        searchText = "Preview",
        setSearchText = { },
        clickSearchButton = { },
        clickBackButton = { },
        focusRequester = FocusRequester()
    )
}