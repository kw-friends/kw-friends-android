package hello.kwfriends.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.ui.screens.main.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    navigation: NavController,
    isSearching: Boolean,
    searchText: String,
    setSearchText: (String) -> Unit,
    clickSearchButton: () -> Unit,
    clickBackButton: () -> Unit,
    focusRequester: FocusRequester,
    currentDestination: String?
) {
    TopAppBar(
        title = {
            Crossfade(targetState = currentDestination, label = "") { destination ->
                when (destination) {
                    "home" -> {
                        Text(
                            text = "홈",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.W600,
                            maxLines = 1
                        )
                    }

                    "findGathering" -> {
                        Text(
                            text = "모임 찾기",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.W600,
                            maxLines = 1
                        )
                    }

                    "chat" -> {
                        Text(
                            text = "채팅",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.W600,
                            maxLines = 1
                        )
                    }
                }
            }

        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                if (isSearching) {
                    IconButton(
                        onClick = clickBackButton,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Exit",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Box(if (isSearching) Modifier.weight(1f) else Modifier) {
                    Row(
                        Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SearchTextField(
                            value = searchText,
                            onValueChange = setSearchText,
                            enable = isSearching,
                            modifier = Modifier
                                .animateContentSize(animationSpec = tween(easing = LinearOutSlowInEasing))
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
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                }
                IconButton(
                    onClick = { navigation.navigate(Routes.SETTINGS_SCREEN) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account",
                        modifier = Modifier
                            .size(24.dp)
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
    MainTopAppBar(
        navigation = navController,
        isSearching = false,
        searchText = "Preview",
        setSearchText = { },
        clickSearchButton = { },
        clickBackButton = { },
        focusRequester = FocusRequester(),
        currentDestination = null
    )
}