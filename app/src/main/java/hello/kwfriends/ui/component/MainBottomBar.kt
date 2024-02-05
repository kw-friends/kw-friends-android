package hello.kwfriends.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import hello.kwfriends.ui.screens.main.MainDestination

@Composable
fun HomeBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (MainDestination) -> Unit
) {
    NavigationBar(modifier = Modifier.zIndex(1f)) {
        MainDestination.entries.forEach { destination ->
            val selected =
                currentDestination?.hierarchy?.any { it.route == destination.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = destination.labelResId),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected) FontWeight.W600 else FontWeight.W500
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun HomeBottomBarPreview() {
    HomeBottomBar(
        currentDestination = null,
        onNavigate = {}
    )
}